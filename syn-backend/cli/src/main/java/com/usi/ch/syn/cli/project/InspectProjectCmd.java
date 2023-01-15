package com.usi.ch.syn.cli.project;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.change.FileMoving;
import com.usi.ch.syn.core.model.change.FileRenaming;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import picocli.CommandLine;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "inspect", description = "Print the git history of a file")
public class InspectProjectCmd implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", paramLabel = "PROJECT_ID", description = "The id of the project that must be analyzed")
    int projectID;

    @CommandLine.Parameters(index = "1", paramLabel = "FILE_HISTORY_ID", description = "The id of the fileHistory")
    int fileHistoryID;

    @CommandLine.Option(names = "--help", usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = { "-g", "--git" }, description = "Check the match with the git History")
    boolean gitCheck = false;


    @Override public Integer call() throws InterruptedException {
        Optional<Project> optProject = ProjectFactory.getInstance().getProject(projectID);
        if (optProject.isPresent()) {
            Project project = optProject.get();

            if (project.getProjectHistory() != null) {
                List<FileHistory> fileHistoryList = project.getProjectHistory().getFileHistories();
                if (fileHistoryID < fileHistoryList.size() && fileHistoryList.get(fileHistoryID) != null) {
                    FileHistory fileHistory = fileHistoryList.get(fileHistoryID);

                    int aliasIndex = 0;
                    System.out.printf("-------- Path: %s -------- %n", fileHistory.getAliases().get(aliasIndex));
                    List<GitCommit> gitCommitList = new ArrayList<>();
                    GitProject gitProject = null;
                    int gitCommitListIndex = 0;

                    if (gitCheck) {
                        gitProject = new JGitProject(project);
                        gitCommitList = gitProject.getCommits(fileHistory.getAliases().get(aliasIndex));
                        Collections.reverse(gitCommitList);
                    }

                    for (FileVersion fileVersion : fileHistory.getFileVersions()) {
                        Change change = fileVersion.getChange();
                        ProjectVersion projectVersion = fileVersion.getParentProjectVersion();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


                        String gitCheckString = "";
                        if (gitCheck) {
                            boolean gitMatch = false;
                            GitCommit gitCommit = gitCommitList.get(gitCommitListIndex++);
                            gitMatch = fileVersion.getParentProjectVersion().getCommitHash().equals(gitCommit.getHash());

                            gitCheckString = "(GIT: " + (gitMatch ? " OK " : " NO") + ")";
                        }

                        System.out.printf("%s on %s with commit %s(%s) %s %n",
                                change.getClass().getSimpleName(),
                                simpleDateFormat.format(new Date(projectVersion.getTimestamp() * 1000)),
                                projectVersion.getCommitHash(),
                                projectVersion.getId(),
                                gitCheckString);


                        if (change.isMove() || change.isRename()) {
                            if (change instanceof FileMoving fileMoving) {
                                System.out.printf("\t \t %s => %s", fileMoving.fromPath, fileMoving.toPath);
                            } else if (change instanceof FileRenaming renameChange)  {
                                System.out.printf("\t \t %s => %s", renameChange.fromName, renameChange.toName);
                            }

                            aliasIndex++;
                            System.out.printf("-------- Path: %s -------- %n", fileHistory.getAliases().get(aliasIndex));

                            if (gitCheck) {
                                assert gitProject != null;
                                gitCommitList = gitProject.getCommits(fileHistory.getAliases().get(aliasIndex));
                                gitCommitList = gitCommitList.subList(0, gitCommitList.size() - 1); //This removes the change/rename commit
                                Collections.reverse(gitCommitList);
                                gitCommitListIndex = 0;
                            }
                        }
                    }
                    return 0;
                } else {
                    System.out.printf("Unable to find a fileHistory with id %s. %n", projectID);
                    return 1;
                }
            } else {
                System.out.printf("Project %s needs to be analyzed first. %n", project.getName());
                return 1;
            }
        } else {
            System.out.printf("Unable to find a project with id %s. %n", projectID);
            return 1;
        }
    }
}
