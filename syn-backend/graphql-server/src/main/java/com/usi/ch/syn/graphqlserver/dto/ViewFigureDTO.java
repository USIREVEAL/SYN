package com.usi.ch.syn.graphqlserver.dto;

import com.usi.ch.syn.core.model.view.FigurePosition;
import com.usi.ch.syn.core.model.view.ViewFigure;
import lombok.Getter;


@Getter
public class ViewFigureDTO {

    private final long id;
    private final int fileHistoryId;

    private final FigurePosition position;
    private final String color;
    private final double height;
    private final short age;
    private final boolean enabled;
    private final String shape;
    private final double opacity;
    private final int size;


    public ViewFigureDTO(final ViewFigure viewFigure) {
        this.id = viewFigure.getEntity().getId();
        this.fileHistoryId = viewFigure.getFileHistoryId();
        this.position = viewFigure.getPosition();
        this.height = viewFigure.getHeight();
        this.color = "#" + Integer.toHexString(viewFigure.getColor().getRGB()).substring(2);
        this.shape = viewFigure.getShape();
        this.age = viewFigure.getAge();
        this.enabled = viewFigure.isEnabled();
        this.opacity = viewFigure.getOpacity();
        this.size = viewFigure.getSize();
    }
}
