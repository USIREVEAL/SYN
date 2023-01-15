package com.usi.ch.syn.core.git;

public abstract class GitChange {
    public abstract ChangeType getChangeType();
    public abstract String getOldPath();
    public abstract String getNewPath();
    public abstract int getLinesAdd();
    public abstract int getLinesDel();


    public enum ChangeType {
        ADD,
        MODIFY,
        DELETE,
        RENAME,
    }

}
