package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.project.Project;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class ProjectAnalysisUtils {

    public static void printCommitCSV(Project project, File csvOutputFile) {
        GitProject gitProject = new JGitProject(project);
        List<GitCommit> commitList = gitProject.getCommits();

        try {
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                commitList.stream()
                        .map(commit -> {
                            return commit.getHash() + "," + commit.getTimestamp() + "," + escapeSpecialCharacters(commit.getMessage());
                        })
                        .forEach(pw::println);
            }
        } catch (Exception e) {

        }
    }


    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
