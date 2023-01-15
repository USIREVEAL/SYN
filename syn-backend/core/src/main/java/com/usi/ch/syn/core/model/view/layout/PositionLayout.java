package com.usi.ch.syn.core.model.view.layout;

import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.view.ViewFigure;
import lombok.Getter;

import java.util.List;

public abstract class PositionLayout {

    protected int figureSize;
    protected int figureSpacing;

    public PositionLayout(int figureSize, int figureSpacing) {
        this.figureSize = figureSize;
        this.figureSpacing = figureSpacing;
    }

    public abstract int getGroundSize();
    public abstract void precomputeLayout(final ProjectHistory projectHistory);
    public abstract void applyLayout(final List<ViewFigure> viewFigureList);
}
