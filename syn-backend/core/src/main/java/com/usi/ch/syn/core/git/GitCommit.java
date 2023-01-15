package com.usi.ch.syn.core.git;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class GitCommit {

    private final long timestamp;
    private final String hash;
    private final String message;

    protected GitCommit(long timestamp, String hash, String message) {
        this.timestamp = timestamp;
        this.hash = hash;
        this.message = message;
    }

    public abstract List<? extends GitChange> getChangeList();

    /**
     * This method will run the git checkout command with the hash of this commit
     */
    public abstract void checkout() throws GitException;
}
