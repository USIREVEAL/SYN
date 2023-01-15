package com.usi.ch.syn.core.model.change;

public class FileMoving extends Change {

    public final String fromPath;
    public final String toPath;

    public FileMoving(int id, String fromPath, String toPath) {
        super(id);
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

}
