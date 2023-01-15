package com.usi.ch.syn.core.model.change;

public class FileRenaming extends Change {

    public final String fromName;
    public final String toName;

    public FileRenaming(int id, String fromName, String toName) {
        super(id);
        this.fromName = fromName;
        this.toName = toName;
    }

}
