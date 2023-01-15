package com.usi.ch.syn.core.mapper.strategies;

import com.usi.ch.syn.core.mapper.MapperStrategy;
import com.usi.ch.syn.core.model.Metric;
import org.apache.commons.math3.stat.StatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This strategy normalized the metric values to obtain the resulting double.
 */
public class NormalizerMapperStrategy implements MapperStrategy<Metric, Double> {

    Map<String, Double> normalizedValuesMap = new HashMap<>();

    private final Integer maxValue;
    public NormalizerMapperStrategy(final Integer maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void generateStrategy(final List<Metric> values) {
        double[] arrayV = values.stream().mapToDouble(m -> Double.parseDouble(m.value())).toArray();
        double[] normalizedArray = StatUtils.normalize(arrayV);

        for (int i = 0; i < arrayV.length; i++) {

            double normalizedValue = (normalizedArray[i] * 100);

            normalizedValuesMap.put(String.valueOf(arrayV[i]), normalizedValue);
        }
    }

    @Override
    public Double mapValue(final Metric metric) {
        Double returnVal =  normalizedValuesMap.get(metric.value());

        if (returnVal == null) {
            throw new RuntimeException("Tried to map a missing original value");
        }

        double scalingFactor = maxValue == -1.0 ? 1 : (double) maxValue;
        return returnVal * scalingFactor;
    }

}
