package com.usi.ch.syn.core.model.view;

import lombok.Getter;

import java.util.List;

public class MusicSheet {

    @Getter
    final List<Measure> measures;

    public MusicSheet(List<Measure> measureList) {
        this.measures = measureList;
    }

    public record Measure(int tempo, long timestamp, int note, double amplitude) {}
}
