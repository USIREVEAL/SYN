package com.usi.ch.syn.core.mapper;

import com.usi.ch.syn.core.model.CodeEntity;
import com.usi.ch.syn.core.model.Metric;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;

@Getter
public class MetricMapper  extends Mapper<Metric, Double> {

    private final Function<CodeEntity, Metric> metricExtractor;

    public MetricMapper(ProjectHistory projectHistory,  Function<ProjectHistory, List<? extends CodeEntity>> entityRetriever, Function<CodeEntity, Metric> metricExtractor, MapperStrategy<Metric, Double> valueMapper)  throws MapperException {
        super(projectHistory, entityRetriever, metricExtractor, valueMapper);
        this.metricExtractor = metricExtractor;
    }

    public static MetricMapper createOnFileVersionMetric(ProjectHistory projectHistory, Function<CodeEntity, Metric> valueRetriever, MapperStrategy<Metric, Double> valueMapper)  throws MapperException {
        Function<ProjectHistory, List<? extends CodeEntity>> entityRetriever = ProjectHistory::getAllFileVersions;
        return new MetricMapper(projectHistory, entityRetriever, valueRetriever, valueMapper);
    }
}
