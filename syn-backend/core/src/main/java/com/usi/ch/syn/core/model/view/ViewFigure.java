package com.usi.ch.syn.core.model.view;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.EntityFigure;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.version.FileVersion;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.List;


@Getter
public class ViewFigure extends EntityFigure {

    @Setter
    private FigurePosition position;
    private final Color color;
    private final double height;
    private final String shape;
    private final short age;
    private final boolean enabled;
    private final double opacity;
    private final int size;
    private final int fileHistoryId;

    /**
     * This constructor should be used to instantiate a new Figure that represents a FileVersion
     *
     * @param fileVersion
     * @param color
     * @param height
     * @param shape
     * @param age
     * @param enabled
     * @param opacity
     * @param size
     */
    public ViewFigure(FileVersion fileVersion, Color color, double height, String shape, short age, boolean enabled, double opacity, int size) {
        super(fileVersion);
        this.position = FigurePosition.ORIGIN;
        this.color = color;
        this.height = height;
        this.shape = shape;
        this.age = age;
        this.enabled = enabled;
        this.opacity = opacity;
        this.size = size;
        this.fileHistoryId = fileVersion.getFileHistory().getId();
    }

    /**
     * This constructor is used to instantiate a new figure that represents a project, in the visualization it's 'the ground.
     */
    public ViewFigure(Project project, int size) {
        super(project);
        this.position = FigurePosition.ORIGIN;
        this.height = 1;
        this.color = Color.darkGray;
        this.shape = "GROUND";
        this.age = 0;
        this.enabled = true;
        this.opacity = 1;
        this.size = size;
        this.fileHistoryId = -1;

    }
}
