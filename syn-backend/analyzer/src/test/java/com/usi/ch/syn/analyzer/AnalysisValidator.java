package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnalysisValidator {
    private static final Logger logger = LogManager.getLogger(AnalysisValidator.class);

    public AnalysisValidator() {}


    public void isAnalysisValid(final ProjectAnalysisResult analysisResults) {
        assertAll("AnalysisValid",
                () -> noMissingProjectVersion(analysisResults),
                () -> projectVersionsAreConnected(analysisResults),
                //() -> noEmptyProjectVersion(project),
                () -> noMissingFileHistory(analysisResults),
                //() -> noMissingFileVersion(project), //Empty commits are removed
                () -> fileHistoryStartsWithAdd(analysisResults),
                () -> fileHistoryHasOnlyOneAdd(analysisResults),
                () -> fileHistoryHasMaxOneDel(analysisResults),
                () -> fileHistoryIsCoherent(analysisResults),
                () -> fileHistoriesHaveRightOrder(analysisResults),
                () -> checkFileHistoryCorrectness(analysisResults)
                //() -> allFilesOfLastCommitExist(project)
        );
    }
    
    private void noMissingProjectVersion(final ProjectAnalysisResult projectAnalysisResult) {
        assertEquals(
                projectAnalysisResult.getProjectVersions().size(),
                projectAnalysisResult.getProjectVersions().get(projectAnalysisResult.getProjectVersions().size() - 1).getId()
        );
    }

    private void projectVersionsAreConnected(final ProjectAnalysisResult projectAnalysisResult) {
        List<ProjectVersion> projectVersionList = projectAnalysisResult.getProjectVersions();

        int projectVersionSize = projectAnalysisResult.getProjectVersions().size();
        if (projectVersionSize > 2) {
            for (int i = 1; i < projectVersionSize - 1; i++) {
                ProjectVersion projectVersion = projectVersionList.get(i);
                assertNotNull(projectVersion.getPrevious());
                assertNotNull(projectVersion.getNext());
                assertEquals(projectVersion, projectVersion.getPrevious().getNext());
                assertEquals(projectVersion, projectVersion.getNext().getPrevious());
            }

            assertEquals(projectVersionList.get(1), projectVersionList.get(0).getNext());
            assertEquals(projectVersionList.get(projectVersionSize - 2), projectVersionList.get(projectVersionSize - 1).getPrevious());
        }
    }

    private void noMissingFileHistory(final ProjectAnalysisResult projectAnalysisResult) {
        assertEquals(
                projectAnalysisResult.getFileHistories().size(),
                projectAnalysisResult.getFileHistories().get(projectAnalysisResult.getFileHistories().size() - 1).getId()
        );
    }

    private void noMissingFileVersion(final ProjectAnalysisResult projectAnalysisResult) {
        assertEquals(
                projectAnalysisResult.getFileVersions().size(),
                projectAnalysisResult.getFileVersions().get(projectAnalysisResult.getFileVersions().size() - 1).getId()
        );
    }

    private void fileHistoryStartsWithAdd(final ProjectAnalysisResult projectAnalysisResult) {
        assertTrue(
                projectAnalysisResult
                        .getFileHistories()
                        .parallelStream()
                        .allMatch(fileHistory ->
                                fileHistory.getFileVersions().get(0).getChange().isAdd()
                        )
        );
    }

    private void fileHistoryHasOnlyOneAdd(final ProjectAnalysisResult projectAnalysisResult) {
        projectAnalysisResult.getFileHistories().forEach(fileHistory -> {
            long numberOfAdd = fileHistory.getFileVersions().stream().filter(fileVersion -> fileVersion.getChange().isAdd()).count();
            assertEquals(1, numberOfAdd);
        });
    }

    private void fileHistoryHasMaxOneDel(final ProjectAnalysisResult projectAnalysisResult) {
        projectAnalysisResult.getFileHistories().forEach(fileHistory -> {
            long numberOfDel = fileHistory.getFileVersions().stream().filter(fileVersion -> fileVersion.getChange().isDelete()).count();
            assertTrue(numberOfDel <= 1);
            if (numberOfDel == 1)  {
                assertTrue(fileHistory.getFileVersions().get(fileHistory.getFileVersions().size() - 1).getChange().isDelete());
            }
        });
    }

    private void noEmptyProjectVersion(final ProjectAnalysisResult projectAnalysisResult) {
        assertTrue(
                projectAnalysisResult
                        .getProjectVersions()
                        .parallelStream()
                        .noneMatch(projectVersion -> projectVersion.getFileVersions().isEmpty())
        );
    }

    private void fileHistoriesHaveRightOrder(final ProjectAnalysisResult projectAnalysisResult) {

        AtomicInteger fileHistoryIDCounter = new AtomicInteger(1);
        projectAnalysisResult.getFileVersions()
                .stream()
                .filter(fileVersion -> fileVersion.getChange().isAdd())
                .sorted()
                .distinct()
                .forEach(fileVersion -> {
                    assertEquals(fileHistoryIDCounter.getAndIncrement(), fileVersion.getFileHistory().getId());
                });


    }

//    private void allFilesOfLastCommitExist(final ProjectAnalysisResult projectAnalysisResult) {
//        ProjectHistory projectHistory = project.getProjectHistory();
//        assertTrue(() -> {
//            Optional<? extends ProjectVersion> lastVersionOverview = projectHistory
//                    .getBaseHistoryView().getChronologicalProjectVersion(projectHistory.getBaseHistoryView().getProjectVersions().size() - 1);
//
//            return lastVersionOverview.map(projectVersion ->
//                projectVersion
//                .getFileVersions()
//                .parallelStream()
//                .allMatch(fileVersion -> {
//                    if (fileVersion.getChange().isDelete()) {
//                        return true;
//                    } else {
//                        return Files.exists(Path.of(project.getPath() + "/" + fileVersion.getFileHistory().getPath()));
//                    }
//                })).orElse(true);
//        });
//    }

    private void fileHistoryIsCoherent(final ProjectAnalysisResult projectAnalysisResult) {
        projectAnalysisResult.getFileHistories().forEach(fileHistory -> {
            int fileVersionsToCheck = fileHistory.getFileVersions().size() < 2 ? 0 : fileHistory.getFileVersions().size() - 2;
            assertTrue(
                    fileHistory
                            .getFileVersions()
                            .parallelStream()
                            .skip(1)
                            .limit(fileVersionsToCheck)
                            .peek(fileVersion -> {
                                        if (fileVersion.getChange().isAdd() || fileVersion.getChange().isDelete()) {
                                            System.out.println(fileVersion);
                                        }
                                    }
                            )
                            .noneMatch(fileVersion ->
                                    fileVersion.getChange().isAdd() || fileVersion.getChange().isDelete()
                            )
            );
        });
    }

    private void checkFileHistoryCorrectness(final ProjectAnalysisResult projectAnalysisResult) {
        List<FileHistory> fileHistories = projectAnalysisResult.getFileHistories();
        for (FileHistory fileHistory : fileHistories) {
            verifyFileHistoryWithGitLog(projectAnalysisResult, fileHistory);
        }
    }

    private void verifyFileHistoryWithGitLog(final ProjectAnalysisResult projectAnalysisResult, final FileHistory fileHistory) {
        int aliasIndex = 0;
        List<GitCommit> gitCommitList = new ArrayList<>();
        GitProject gitProject = null;
        int gitCommitListIndex = 0;

        gitProject = new JGitProject(projectAnalysisResult.getProject());
        gitCommitList = gitProject.getCommits(fileHistory.getAliases().get(aliasIndex));
        Collections.reverse(gitCommitList);


        for (FileVersion fileVersion : fileHistory.getFileVersions()) {
            Change change = fileVersion.getChange();
            ProjectVersion projectVersion = fileVersion.getParentProjectVersion();


            boolean gitMatch = false;
            GitCommit gitCommit = gitCommitList.get(gitCommitListIndex++);
            gitMatch = fileVersion.getParentProjectVersion().getCommitHash().equals(gitCommit.getHash());
            if (!gitMatch) {
                logger.error("[FileHistory {}] Missing match between fileVersion {} with parent {} and git log with hash {}",
                        fileHistory.getId(),
                        fileVersion.getId(),
                        fileVersion.getParentProjectVersion().getCommitHash(),
                        gitCommit.getHash());
            }
            assertTrue(gitMatch);


            if (change.isMove() || change.isRename()) {
                aliasIndex++;
                gitCommitList = gitProject.getCommits(fileHistory.getAliases().get(aliasIndex));
                gitCommitList = gitCommitList.subList(0, gitCommitList.size() - 1); //This removes the change/rename commit
                Collections.reverse(gitCommitList);
                gitCommitListIndex = 0;

            }
        }
    }

    public static void spotDifferencesBetweenAnalysis(ProjectAnalysisResult analysis1, ProjectAnalysisResult analysis2) {
        List<ProjectVersion> projectVersions1 = analysis1.getProjectVersions();
        List<ProjectVersion> projectVersions2 = analysis2.getProjectVersions();
        for(int i = 0; i < projectVersions1.size(); i++) {
            ProjectVersion projectVersion1 = projectVersions1.get(i);
            ProjectVersion projectVersion2 = projectVersions2.get(i);
            if (!projectVersion1.equals(projectVersion2)) {
                spotDifferencesBetweenProjectVersions(projectVersion1, projectVersion2);
            }
        }

        List<FileHistory> fileHistories1 = analysis1.getFileHistories();
        List<FileHistory> fileHistories2 = analysis2.getFileHistories();
        for(int i = 0; i < fileHistories1.size(); i++) {
            FileHistory fileHistory1 = fileHistories1.get(i);
            FileHistory fileHistory2 = fileHistories2.get(i);
            if (!fileHistory1.equals(fileHistory2)) {
                spotDifferencesBetweenFileHistories(fileHistory1, fileHistory2);
            }
        }
    }

    private static void spotDifferencesBetweenProjectVersions(ProjectVersion projectVersion1, ProjectVersion projectVersion2) {


        logger.error("----- Found two different ProjectVersions with id: {}, {}", projectVersion1.getId(), projectVersion1.getId());

        if (!(projectVersion1.getTimestamp() == projectVersion2.getTimestamp())) {
            logger.error("ProjectVersion-Diff timestamp: {}, {}", projectVersion1.getTimestamp(), projectVersion2.getTimestamp());
        }

        if (!(projectVersion1.getCommitHash().equals(projectVersion2.getCommitHash()))) {
            logger.error("ProjectVersion-Diff commitHash: {}, {}", projectVersion1.getCommitHash(), projectVersion2.getCommitHash());
        }

        if (!(projectVersion1.getCommitMessage().equals(projectVersion2.getCommitMessage()))) {
            logger.error("ProjectVersion-Diff commitMessage: {}, {}", projectVersion1.getCommitMessage(), projectVersion2.getCommitMessage());
        }

        if (!(projectVersion1.getFileVersions().equals(projectVersion2.getFileVersions()))) {
            if (projectVersion1.getFileVersions().size() != projectVersion2.getFileVersions().size())
                logger.error("ProjectVersion-Diff getFileVersions size: {}, {}", projectVersion1.getFileVersions().size(), projectVersion2.getFileVersions().size());



            List<FileVersion> fileVersions1 = projectVersion1.getFileVersions();
            List<FileVersion> fileVersions2 = projectVersion2.getFileVersions();

            for(int i = 0; i < fileVersions1.size(); i++) {
                FileVersion fileVersion1 = fileVersions1.get(i);
                FileVersion fileVersion2 = fileVersions2.get(i);
                if (!fileVersion1.equals(fileVersion2)) {
                    spotDifferencesBetweenFileVersions(fileVersion1, fileVersion2);
                }
            }
        }


    }

    private static void spotDifferencesBetweenFileHistories(FileHistory fileHistory1, FileHistory fileHistory2) {

        logger.error("----- Found two different FileHistories with id: {}, {}", fileHistory1.getId(), fileHistory2.getId());

        if (!fileHistory1.getName().equals(fileHistory2.getName())) {
            logger.error("FileHistory-Diff names: {}, {}", fileHistory1.getName(), fileHistory2.getName());
        }

        if (!fileHistory1.getPath().equals(fileHistory2.getPath())) {
            logger.error("FileHistory-Diff paths: {}, {}", fileHistory1.getPath(), fileHistory2.getPath());
        }

        if (!fileHistory1.getFileVersions().equals(fileHistory2.getFileVersions())) {
            List<FileVersion> fileVersions1 = fileHistory1.getFileVersions();
            List<FileVersion> fileVersions2 = fileHistory2.getFileVersions();

            for(int i = 0; i < fileVersions1.size(); i++) {
                FileVersion fileVersion1 = fileVersions1.get(i);
                FileVersion fileVersion2 = fileVersions2.get(i);
                if (!fileVersion1.equals(fileVersion2)) {
                    spotDifferencesBetweenFileVersions(fileVersion1, fileVersion2);
                }
            }
        }

        if (!fileHistory1.getAliases().equals(fileHistory2.getAliases())) {
            logger.error("FileHistory-Diff aliases list: {}, {}", fileHistory1.getAliases(), fileHistory2.getAliases());
        }

        if (!fileHistory1.getFileTypes().equals(fileHistory2.getFileTypes())) {
            logger.error("FileHistory-Diff fileTypes list: {}, {}", fileHistory1.getFileTypes(), fileHistory2.getFileTypes());

        }
    }

    private static void spotDifferencesBetweenFileVersions(FileVersion fileVersion1, FileVersion fileVersion2) {
        logger.error("Found two different FileVersion with id: {}, {}", fileVersion1.getId(), fileVersion2.getId());

        if (!fileVersion1.getParentProjectVersion().equals(fileVersion2.getParentProjectVersion())) {
            logger.error("FileVersion-Diff ParentProjectVersion with id: {}, {}", fileVersion1.getParentProjectVersion().getId(), fileVersion2.getParentProjectVersion().getId());
        }

        if (!fileVersion1.getChange().equals(fileVersion2.getChange())) {
            logger.error("FileVersion-Diff Change: {}, {}", fileVersion1.getChange(), fileVersion2.getChange());
        }

    }

}
