package com.usi.ch.syn.core.git;

import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.Project;
import lombok.Getter;

import java.util.List;

public abstract class GitProject {

    @Getter private final Project project;

    public GitProject(final Project project) {
        this.project = project;
    }

    /**
     * This method will close the resource that has been used to access the repository
     */
    public abstract void closeResource();

    /**
     * This method is used to retrieve the full list of commits except for merge commits.
     * @return A list of GitCommit
     */
    public List<GitCommit> getCommits() {
        return getCommits("", "");
    }

    /**
     * This method should be used to retrieve a list of commits within the commit represented by the mostRecentCommitHash and the commit represented by the leastRecentCommitHash.
     *
     * @param mostRecentCommitHash the hash of the most recent commit
     * @param leastRecentCommitHash the hash of the least recent commit
     * @return A list of GitCommit
     */
    public abstract List<GitCommit> getCommits(final String mostRecentCommitHash, final String leastRecentCommitHash);

    /**
     * This method will return a list of commit that will be used by the analyzer.
     * @param analysisWorkDescriptor The descriptor of the work that will be done by the analysis containing a list of commit hashes.
     * @return A list of GitCommit
     */
    public abstract List<GitCommit> getCommits(AnalysisWorkDescriptor analysisWorkDescriptor) throws GitException;

    /**
     * This method will return a list of commits involving a specific path
     * @param path A path of a file
     * @return A list of GitCommit that acted with that path
     */
    public abstract List<GitCommit> getCommits(String path);
}
