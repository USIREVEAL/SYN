package com.usi.ch.syn.core.mapper.strategies;

import com.usi.ch.syn.core.mapper.MapperStrategy;
import com.usi.ch.syn.core.model.Metric;

import java.util.List;

/**
 * This strategy maps the value of a metric to double obtained by a linear function
 */
public class LinearMapperStrategy implements MapperStrategy<Metric, Double> {

    private long max = Long.MIN_VALUE;
    private final Integer maxValue;

    public LinearMapperStrategy(Integer maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void generateStrategy(final List<Metric> values) {
        if (!values.isEmpty()) {
            this.max = values.stream().mapToLong(m -> Long.parseLong(m.value())).max().getAsLong();
        } else {
            throw new RuntimeException("Trying to generate a mapper with an empty set");
        }

    }

    @Override
    public Double mapValue(final Metric metric) {
        if (max == Long.MIN_VALUE) {
            throw new RuntimeException("mapValue method called before strategy generation");
        }

        int x = Integer.parseInt(metric.value());
        double scalingFactor = maxValue == -1.0 ? 1 : (double) maxValue / max;
        double mappedValue = (double) (x + 5) / max;

        return mappedValue * scalingFactor;
    }

}
