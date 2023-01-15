package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.git.GitChange;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitException;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.metric.FileMetricCalculator;
import com.usi.ch.syn.core.model.Metric;
import com.usi.ch.syn.core.model.analysis.*;
import com.usi.ch.syn.core.model.change.*;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.storage.StorageHelper;
import com.usi.ch.syn.core.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.usi.ch.syn.core.utils.Config.SYN_DATA_PATH;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ProjectAnalyzer extends Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(ProjectAnalyzer.class);

    private final Map<String, FileHistory> fileHistoryMap = new HashMap<>();
    private Project project;
    private ProjectVersion previousVersion = null;
    private ProjectAnalysisResult projectAnalysis;
    private List<GitCommit> commitList;
    private String status = "";
    private final Thread analysisThread = Thread.currentThread();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    final Runnable storeBackup = () -> {
        Path dirBackupPath =  Path.of(SYN_DATA_PATH + File.separator + "tmp_backup_" + project.getName());
        try {
            if (Files.notExists(dirBackupPath))
                Files.createDirectory(dirBackupPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path tmpFilePath =  Path.of(dirBackupPath + File.separator + project.getId() + ".json");
        StorageHelper.storeProject(projectAnalysis, tmpFilePath.toFile());
    };

    @Override
    public ProjectAnalysisResult runAnalysis(AnalysisWorkDescriptor analysisWorkDescriptor, BlockingQueue<AnalysisStatus> analysisStatusQueue) {
        final Runnable updateStatus = () -> {analysisStatusQueue.add(getAnalysisStatus());};
        final ScheduledFuture<?> updateStatusHandler = scheduler.scheduleAtFixedRate(updateStatus, 0, 5, SECONDS);
        ProjectAnalysisResult projectAnalysisResult = runAnalysis(analysisWorkDescriptor);
        analysisStatusQueue.add(getAnalysisStatus());
        updateStatusHandler.cancel(true);
        return projectAnalysisResult;
    }

    private AnalysisStatus getAnalysisStatus() {
        if (projectAnalysis != null) {
            int analyzedCommits = projectAnalysis.getProjectVersions().size();

            return new AnalysisStatus(
                    analysisThread,
                    analyzedCommits,
                    commitList.size() - analyzedCommits,
                    projectAnalysis.getFileHistories().size(),
                    projectAnalysis.getFileVersions().size(),
                    status
            );
        } else {
            return new AnalysisStatus(
                    analysisThread,
                    0,
                    0,
                    0,
                    0,
                    status
            );
        }

    }

    /**
     * Method that runs a single thread analysis of the project given an interval specified by commits
     */
    @Override
    public ProjectAnalysisResult runAnalysis(AnalysisWorkDescriptor analysisWorkDescriptor) {
        this.project = analysisWorkDescriptor.project();
        status = "GIT_INITIALIZING";
        GitProject gitProject = new JGitProject(project);
        status = "COMMITS_FETCHING";
        try {
            commitList = gitProject.getCommits(analysisWorkDescriptor);
        } catch (GitException e) {
            status = "ERROR";
            e.printStackTrace();
        }
        status = "RUNNING";
        if (commitList.size() > 1) {
            final ScheduledFuture<?> updateStatusHandler = scheduler.scheduleAtFixedRate(storeBackup, 0, 5, MINUTES);
            String firstCommitHash = commitList.get(0).getHash();
            String lastCommitHash = commitList.get(commitList.size() - 1).getHash();
            logger.info("Analyzing project {} from commit {} to commit {}", project.getName(), firstCommitHash, lastCommitHash);
            projectAnalysis = ProjectAnalysisResult.buildPartialAnalysisResult(project);
            projectAnalysis.setFirstCommit(firstCommitHash);
            logger.info("Found {} commits", commitList.size());
            for (GitCommit gitCommit : commitList) {
                try {
                    commitToProjectVersion(gitCommit);
                } catch (GitException exception) {
                    exception.printStackTrace();
                    status = "ERROR";
                    break;
                }
            }
            updateStatusHandler.cancel(true);
            gitProject.closeResource();

            if (!status.equals("ERROR")) {
                status = "COMPLETED";
            }
            return projectAnalysis;
        } else {
            logger.error("Unable to analyze repositories with less than 2 commits.");
            return null;
        }

    }


    private void commitToProjectVersion(final GitCommit commit) throws GitException {
        List<? extends GitChange> gitChangeList = commit.getChangeList();
        if (gitChangeList.size() > 0) {
            ProjectVersion projectVersion = projectAnalysis.createNewProjectVersion(
                    commit.getTimestamp(),
                    commit.getHash(),
                    commit.getMessage()
            );

            if (previousVersion != null) previousVersion.setNext(projectVersion);
            projectVersion.setPrevious(previousVersion);
            previousVersion = projectVersion;

            commit.checkout();
            gitChangeList.stream()
                    .map(this::gitChangeToFileVersion)
                    .forEach(projectVersion::addFileVersion);

            projectAnalysis.setLastCommit(commit.getHash());
        }
    }


    private FileVersion gitChangeToFileVersion(final GitChange gitChange) {
        Change change;
        FileHistory fileHistory;

        switch (gitChange.getChangeType()) {
            case RENAME -> {
                String oldFilePath = gitChange.getOldPath();
                String newFilePath = gitChange.getNewPath();

                if (!fileHistoryMap.containsKey(oldFilePath)) {
                    fileHistory = projectAnalysis.createNewFileHistory(new File(newFilePath).getName(), newFilePath);
                    fileHistory.getAliases().add(0, oldFilePath);
                } else {
                    fileHistory = fileHistoryMap.get(oldFilePath);
                    fileHistory.setNewPath(newFilePath);
                    fileHistoryMap.remove(oldFilePath);
                }

                fileHistoryMap.put(newFilePath, fileHistory);
                File oldFile = new File(oldFilePath);
                File newFile = new File(newFilePath);
                if (newFile.getName().equals(oldFile.getName())) {
                    change = new FileMoving(-1, oldFilePath, newFilePath);
                } else {
                    change = new FileRenaming(-1, oldFilePath, newFilePath);
                }
            }
            case MODIFY -> {
                String oldFilePath = gitChange.getOldPath();
                fileHistory = fileHistoryMap.computeIfAbsent(oldFilePath, fileHistoryPath -> projectAnalysis.createNewFileHistory(new File(oldFilePath).getName(), fileHistoryPath));
                change = new FileModification(-1);
            }
            case ADD -> {
                String newFilePath = gitChange.getNewPath();
                File fileHistoryFile = new File(newFilePath);
                fileHistory = fileHistoryMap.computeIfAbsent(newFilePath, fileHistoryPath -> projectAnalysis.createNewFileHistory(fileHistoryFile.getName(), fileHistoryPath));
                change = new FileAddition(-1);
            }
            case DELETE -> {
                String oldFilePath = gitChange.getOldPath();
                fileHistory = fileHistoryMap.computeIfAbsent(oldFilePath, fileHistoryPath -> projectAnalysis.createNewFileHistory(new File(oldFilePath).getName(), fileHistoryPath));
                //fileHistoryMap.remove(oldFilePath);
                change = new FileDeletion(-1);
            }
            default -> throw new IllegalStateException("Unexpected value: " + gitChange.getChangeType());
        }


        change.setLinesAdded(gitChange.getLinesAdd());
        change.setLinesDeleted(gitChange.getLinesDel());

        FileVersion fileVersion = projectAnalysis.createNewFileVersion(change, fileHistory);

        if (!change.isDelete()) {
            File fileHistoryFile = new File(project.getPath() + '/' + fileHistory.getPath());
            List<FileMetricCalculator> metricsToBeComputed = FileTypeManager.getMetricsToBeComputed(fileHistory);
            List<Metric> metricList = metricsToBeComputed.stream().map(fvm -> fvm.calculate(fileHistoryFile)).distinct().toList();
            fileVersion.getMetrics().addAll(metricList);
        }

        return fileVersion;
    }

}
