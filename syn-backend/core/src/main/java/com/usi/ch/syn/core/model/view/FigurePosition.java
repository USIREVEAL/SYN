package com.usi.ch.syn.core.model.view;

public record FigurePosition(int x, int y, int z) {

    public FigurePosition add(FigurePosition other) {
        return new FigurePosition(x + other.x(), y + other.y(), z + other.z());
    }

    public static final FigurePosition ORIGIN = new FigurePosition(0, 0, 0);

}