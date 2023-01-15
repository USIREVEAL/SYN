package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.Project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class is responsible to create AnalysisWorkDescriptor
 */
public class JGitAnalysisWorkerDescriptorFactory {

    /**
     * This method splits the history into multiple chunks each one representing an AnalysisWorkDescriptor
     * @param project a project
     * @param chunks the number of chunks to split the history into
     * @return a List of AnalysisWorkDescriptor
     */
    public List<AnalysisWorkDescriptor> createOnProjectHistoryChunks(Project project, int chunks) {
        GitProject gitProject = new JGitProject(project);

        List<GitCommit> unsortedCommitList = gitProject.getCommits();
        List<GitCommit> commitList = unsortedCommitList.stream().sorted(Comparator.comparing(GitCommit::getTimestamp)).toList();

        int numberOfCommits = commitList.size();
        int chunkSize = numberOfCommits / chunks;

        List<AnalysisWorkDescriptor> analysisWorkDescriptors = new ArrayList<>();
        for (int i = 0; i < chunks - 1; i++) {
            List<String> commitHashList = commitList
                    .subList(i * chunkSize, (i + 1) * chunkSize)
                    .stream()
                    .map(GitCommit::getHash)
                    .toList();

            analysisWorkDescriptors.add(new AnalysisWorkDescriptor(project, commitHashList));
        }

        List<String> commitHashList = commitList
                .subList((chunks - 1) * chunkSize, numberOfCommits)
                .stream()
                .map(GitCommit::getHash)
                .toList();

        analysisWorkDescriptors.add(new AnalysisWorkDescriptor(project, commitHashList));

        return analysisWorkDescriptors;
    }

    /**
     * This method returns a AnalysisWorkDescriptor within the given commit boundaries
     * @param project a project
     * @param mostRecentCommit the most recent commit of the descriptor
     * @param leastRecentCommit the least recent commit of the descriptor
     * @return an AnalysisWorkDescriptor
     */
    public AnalysisWorkDescriptor createWithCommitBoundaries(Project project, String mostRecentCommit, String leastRecentCommit) {
        GitProject gitProject = new JGitProject(project);
        List<String> hashList = gitProject
                .getCommits(mostRecentCommit, leastRecentCommit)
                .stream()
                .map(GitCommit::getHash)
                .toList();

        return new AnalysisWorkDescriptor(project, hashList);
    }

}

