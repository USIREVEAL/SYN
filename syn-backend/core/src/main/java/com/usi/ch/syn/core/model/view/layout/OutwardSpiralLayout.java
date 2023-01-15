package com.usi.ch.syn.core.model.view.layout;


import com.usi.ch.syn.core.model.EntityIdTranslator;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.view.FigurePosition;
import com.usi.ch.syn.core.model.view.ViewFigure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutwardSpiralLayout extends PositionLayout {

    private final Map<FileHistory, FigurePosition> fileHistoryPositionMap = new HashMap<>();
    private int segmentLength = 1;

    public OutwardSpiralLayout(int figureSize, int figureSpacing) {
        super(figureSize, figureSpacing);
    }

    @Override
    public int getGroundSize() {
        return (int) ((segmentLength * 1.2) * (figureSize + figureSpacing));
    }

    @Override
    public void precomputeLayout(final ProjectHistory projectHistory) {
        int fileHistoriesCount = projectHistory.getFileHistories().size();
        FigurePosition direction = new FigurePosition(figureSize + figureSpacing, 0, 0);
        FigurePosition currentPosition = new FigurePosition(0, 0, 0);
        int segmentComputed = 0;

        for (int i = 1; i <= fileHistoriesCount; i++) {
            currentPosition = currentPosition.add(direction);
            segmentComputed++;

            int fileHistoryId =  EntityIdTranslator.generateEntityId(FileHistory.class, i);
            fileHistoryPositionMap.put(projectHistory.getFileHistoryByID(fileHistoryId), currentPosition);

            if (segmentComputed == segmentLength) {
                segmentComputed = 0;
                direction = new FigurePosition(-direction.z(), 0, direction.x());
                if (direction.x() == 0 || direction.z() == 0) {
                    segmentLength++;
                }
            }
        }
    }

    @Override
    public void applyLayout(List<ViewFigure> viewFigureList) {
        for (ViewFigure viewFigure : viewFigureList) {
            // TODO: Cast might introduce runtime overhead
            FileVersion fileVersion = (FileVersion) viewFigure.getEntity();
            viewFigure.setPosition(fileHistoryPositionMap.get(fileVersion.getFileHistory()));
        }
    }
}
