package com.usi.ch.syn.core.metric;

import com.usi.ch.syn.core.model.Metric;

import java.io.File;

public record FileMetricCalculator (String name, FileMetricFunction calculatorFunction) {

    public Metric calculate(File file) {
        return new Metric(name, calculatorFunction.apply(file));
    }
}
