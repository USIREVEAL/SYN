package com.usi.ch.syn.core.model.change;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.change.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Change extends Entity {

    private int linesAdded;
    private int linesDeleted;

    protected Change(int id) {
        super(id);
    }

    public boolean isAdd() {
        return this instanceof FileAddition;
    }

    public boolean isDelete() {
        return this instanceof FileDeletion;
    }

    public boolean isRename() {
        return this instanceof FileRenaming;
    }

    public boolean isMove() {
        return this instanceof FileMoving;
    }

    public boolean isModify() {
        return this instanceof FileModification;
    }
}
