package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


public class ProjectAnalyzerJoiner {
    private static final Logger logger = LoggerFactory.getLogger(ProjectAnalyzerJoiner.class);

    public ProjectAnalysisResult joinAnalysisResults(ProjectAnalysisResult ...argProjectAnalysisResults) {

        ProjectAnalysisResult[] projectAnalysisResults = ensureProjectAnalysisOrder(argProjectAnalysisResults);
        System.out.println("Results ordered");

        if (projectAnalysisResults.length >= 1) {
            logger.info("Joining {} analysis results ", projectAnalysisResults.length);

            Project project = projectAnalysisResults[0].getProject();
            String commitFrom = projectAnalysisResults[0].getFirstCommit();
            String commitTo = projectAnalysisResults[projectAnalysisResults.length - 1].getLastCommit();

            ProjectAnalysisResult projectAnalysisResult = ProjectAnalysisResult.buildCompleteAnalysisResult(project, commitFrom, commitTo);

            for (ProjectAnalysisResult partialAnalysisResult : projectAnalysisResults) {
                Map<FileHistory, FileHistory> partialToNewFileHistoryMap = convertPartialToNewFileHistories(projectAnalysisResult, partialAnalysisResult.getFileHistories());

                partialAnalysisResult.getProjectVersions().forEach(partialProjectVersion -> {
                    ProjectVersion projectVersion = projectAnalysisResult.createNewProjectVersion(
                            partialProjectVersion.getTimestamp(),
                            partialProjectVersion.getCommitHash(),
                            partialProjectVersion.getCommitMessage()
                    );

                    partialProjectVersion.getFileVersions().forEach(partialFileVersion -> {
                        FileHistory partialFileHistory = partialFileVersion.getFileHistory();
                        FileHistory fileHistory = partialToNewFileHistoryMap.get(partialFileHistory);
                        assert (fileHistory != null);
                        FileVersion fileVersion = projectAnalysisResult.createNewFileVersion(partialFileVersion.getChange(), fileHistory);
                        fileVersion.getMetrics().addAll(partialFileVersion.getMetrics());
                        projectVersion.addFileVersion(fileVersion);
                    });
                });
            }

            List<FileHistory> fileHistoryList = getFinalFileHistoryList(projectAnalysisResult);
            List<ProjectVersion> projectVersionList = getFinalProjectVersionList(projectAnalysisResult);

//            ProjectHistory projectHistory = new ProjectHistory(fileHistoryList, projectVersionList);
//            projectAnalysisResult.setProjectHistory(projectHistory);

            logger.info("Analysis of {} completed", projectAnalysisResult.getProject().getName());

            return projectAnalysisResult;

        } else {
            throw new IllegalArgumentException("Invalid argument passed to joinAnalysisResults. You should pass at least one ProjectAnalysisResult and all ProjectAnalysisResult must be computed on the same project.");
        }
    }

    private ProjectAnalysisResult[] ensureProjectAnalysisOrder(final ProjectAnalysisResult[] projectAnalysisResults) {
//        if (projectAnalysisResults.length >= 1) {
//            logger.info("Checking the order of the ProjectAnalysisResults");
//
//            Project project = projectAnalysisResults[0].getProject();
//            GitProject gitProject = new JGitProject(project);
//            List<GitCommit> commitList = gitProject.getCommits();
//            List<String> commitListString = commitList.stream().sorted(Comparator.comparing(GitCommit::getTimestamp)).map(GitCommit::getHash).toList();
//            ProjectAnalysisResult[] orderedProjectAnalysisResults = projectAnalysisResults;
//
//            int commitListIndex = 0;
//            for (int i = 0; i < projectAnalysisResults.length; i++) {
//                if (projectAnalysisResults[i].getFirstCommit().equals(commitListString.get(commitListIndex))) {
//                    orderedProjectAnalysisResults[i] = projectAnalysisResults[i];
//                    commitListIndex = commitListString.indexOf(projectAnalysisResults[i].getLastCommit()) + 1;
//                } else {
//                    String searchedString = commitListString.get(commitListIndex);
//                    Optional<ProjectAnalysisResult> optionalProjectAnalysisResult = Arrays.stream(projectAnalysisResults)
//                            .filter(projectAnalysisResult -> projectAnalysisResult.getFirstCommit().equals(searchedString))
//                            .findFirst();
//
//                    if (optionalProjectAnalysisResult.isPresent()) {
//                        ProjectAnalysisResult projectAnalysisResult = optionalProjectAnalysisResult.get();
//                        orderedProjectAnalysisResults[i] = projectAnalysisResult;
//                        commitListIndex = commitListString.indexOf(projectAnalysisResult.getLastCommit()) + 1;
//                    } else {
//                        System.out.println("Unable to find a ProjectAnalysisResult of commit " +  searchedString + " (index: " + i + ")");
//                        System.exit(-1);
//                    }
//                }
//            }
//            return orderedProjectAnalysisResults;
//        }
        return projectAnalysisResults;
    }

    private List<ProjectVersion> getFinalProjectVersionList(ProjectAnalysisResult projectAnalysisResult) {
        List<ProjectVersion> projectVersionList = projectAnalysisResult
            .getProjectVersions()
            .stream()
            .sorted()
            .collect(Collectors.toCollection(ArrayList::new));

        if (projectVersionList.size() > 2) {
            int projectVersionListSize = projectVersionList.size();
            projectVersionList.get(1).setNext(projectVersionList.get(2));
            for (int i = 1; i < projectVersionListSize; i++) {
                ProjectVersion projectVersion = projectVersionList.get(i);
                projectVersion.setPrevious(projectVersionList.get(i - 1));
                projectVersion.getPrevious().setNext(projectVersion);
            }
        }

        return projectVersionList;
    }

    private List<FileHistory> getFinalFileHistoryList(ProjectAnalysisResult projectAnalysisResult) {
        List<FileHistory> fileHistoryList = new ArrayList<>();

        projectAnalysisResult.getFileHistories().stream()
                .sorted()
                .forEach(fileHistory -> {
                    fileHistoryList.add(fileHistory);
                    linkFileHistoryFileVersions(fileHistory);
                });
        return fileHistoryList;
    }

    /**
     * This function is the core of the join algorithm. The fileHistoryID is crucial in SYN, since it determines the entity position in the visual matrix.
     * This function must be executed before ProjectVersion and FileVersions are joined because it creates the FileHistories that those versions refer upon.
     * The Map returned is a dictionary that maps each partialFileHistory to a newFileHistory, where a partialFileHistory is the FileHistory of a partialAnalysisResult
     * and on the other hand, a newFileHistory is a FileHistory of the newAnalysisResult (the result of the join).
     *
     * In order to identify a partialFileHistory into a list of newFileHistory the following strategy is applied:
     *          Algo.1) If the last alias of a newFileHistory is equals to the fist alias of a partialFileHistory then they represent the same File.
     *          Algo.2) If in the partialAnalysisResult there are more than one FileHistory with the same first alias, only the first is mapped to a
     *          newFileHistory and the others FileHistories will be mapped to completely new newFileHistories.
     *          Algo.3) If a partialFileHistory hasn't any alias match with a previously created newFileHistory it will be mapped to a completely new newFileHistory.
     *
     * @param projectAnalysisResult An existing ProjectAnalysisResult object that contains the previously created newFileHistories
     * @param partialFileHistories A list of partialFileHistories that must be mapped with newFileHistories
     * @return A dictionary that maps each partialFileHistory to a newFileHistory (previously or just created)
     */
        private Map<FileHistory, FileHistory> convertPartialToNewFileHistories(ProjectAnalysisResult projectAnalysisResult, List<FileHistory> partialFileHistories) {
        Map<FileHistory, FileHistory> partialToNewFileHistoryMap = new HashMap<>();
        Map<String, FileHistory> partialFirstAliasFileHistoriesMap = new HashMap<>(); //Algo.1
        List<FileHistory> partialFileHistoryToBeCreated = new ArrayList<>(); //Algo.2

        partialFileHistories.forEach(fileHistory -> {
            String firstAlias = fileHistory.getAliases().get(0);
            if (!partialFirstAliasFileHistoriesMap.containsKey(firstAlias)) {
                partialFirstAliasFileHistoriesMap.put(firstAlias, fileHistory); // Algo.1
            } else {
                partialFileHistoryToBeCreated.add(fileHistory); // Algo.2
            }
        });

        // Algo.1
        projectAnalysisResult.getFileHistories().forEach(newFileHistory -> {
            String lastAlias = newFileHistory.getAliases().get(newFileHistory.getAliases().size() - 1);
            if (partialFirstAliasFileHistoriesMap.containsKey(lastAlias)) {
                FileHistory partialFileHistory = partialFirstAliasFileHistoriesMap.get(lastAlias);
                newFileHistory.setNewPath(partialFileHistory.getPath());
                newFileHistory.getAliases().remove(newFileHistory.getAliases().size() - 1);

                if (partialFileHistory.getAliases().size() > 1)
                    partialFileHistory.getAliases().remove(0);
                newFileHistory.getAliases().addAll(partialFileHistory.getAliases());
                partialToNewFileHistoryMap.put(partialFileHistory, newFileHistory);
                partialFirstAliasFileHistoriesMap.remove(lastAlias);
            }
        });

        // Algo.3
        partialFileHistoryToBeCreated.addAll(partialFirstAliasFileHistoriesMap.values());
        Collections.sort(partialFileHistoryToBeCreated);

        // It creates completely new newFileHistories collected in Algo.2 and Algo.3 steps
        partialFileHistoryToBeCreated.forEach(partialFileHistory -> {
            FileHistory newFileHistory = projectAnalysisResult.createNewFileHistory(
                    partialFileHistory.getName(),
                    partialFileHistory.getPath()
            );

            newFileHistory.setFileTypes(partialFileHistory.getFileTypes());
            newFileHistory.setAliases(partialFileHistory.getAliases());
            partialToNewFileHistoryMap.put(partialFileHistory, newFileHistory);
        });

        assert (partialToNewFileHistoryMap.size() == partialFileHistories.size());
        return partialToNewFileHistoryMap;
    }

    private boolean analysisHaveSameProject(ProjectAnalysisResult[] projectAnalysisResults) {
        if (projectAnalysisResults.length == 0)
            return false;

        Project project = projectAnalysisResults[0].getProject();
        return Arrays.stream(projectAnalysisResults).allMatch(projectAnalysisResult -> project.equals(projectAnalysisResult.getProject()));
    }

    /**
     * This function will link all the fileVersions of a given fileHistory by setting the value of
     * the previous/next field
     * @param fileHistory instance of fileHistory
     */
    private void linkFileHistoryFileVersions(final FileHistory fileHistory) {
        FileVersion previousVersion = null;
        Set<FileVersion> versionsToCheck = new HashSet<>();


        for (FileVersion fileVersion : fileHistory.getFileVersions()) {
            if (previousVersion != null) {
                previousVersion.setNext(fileVersion);
            }
            fileVersion.setPrevious(previousVersion);
            previousVersion = fileVersion;

            if (fileVersion.getChange().isAdd() || fileVersion.getChange().isDelete())
                versionsToCheck.add(fileVersion);
        }

//        versionsToCheck.forEach(fileVersion -> {
//            if (    (fileVersion.getChange().isAdd() && fileVersion.getPrevious() != null) ||
//                    (fileVersion.getChange().isDelete() && fileVersion.getNext() != null)
//            ) {
//                if (fileVersion.getPrevious() != null) fileVersion.getPrevious().setNext(fileVersion.getNext());
//                if (fileVersion.getNext() != null)  fileVersion.getNext().setPrevious(fileVersion.getPrevious());
//                fileVersion.getFileHistory().getFileVersions().remove(fileVersion);
//                fileVersion.getParentProjectVersion().getFileVersions().remove(fileVersion);
//                fileVersion.setNext(null);
//                fileVersion.setPrevious(null);
//            }
//        });
    }


}
