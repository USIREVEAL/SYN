package com.usi.ch.syn.core.model.view;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ViewAnimation extends Entity {

    private List<ProjectVersion> representedEntities;
    private List<ViewFigure> viewFigureList;

    private long tsFrom, tsTo;

    //Internal needed to compute aging (special case: animations grouped by ts, aging by commit and empty frame)
    private long lastProjectVersionID;

    public ViewAnimation(int id) {
        super(id);
    }

}


