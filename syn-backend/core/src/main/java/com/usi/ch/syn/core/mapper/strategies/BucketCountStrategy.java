package com.usi.ch.syn.core.mapper.strategies;
import com.usi.ch.syn.core.mapper.MapperStrategy;
import com.usi.ch.syn.core.model.Metric;
import org.apache.commons.math3.stat.StatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This strategy maps the value of a metric to a double represented by a bucket
 */
public class BucketCountStrategy implements MapperStrategy<Metric, Double> {

    private final Map<String, Integer> valueBucketMap = new HashMap<>();
    private final int buckets;
    private final int stepSize;
    private final Integer maxValue;
    double scalingFactor;

    public BucketCountStrategy() { this(3, -1);  }
    public BucketCountStrategy(int buckets, Integer maxValue) {
        this.buckets = buckets;
        this.stepSize = 100 / buckets;
        this.maxValue = maxValue;
        scalingFactor = maxValue <= 0 ? 1 : (double) maxValue / buckets;
    }

    public Map<String, Integer> getValueBucketMap() {
        return valueBucketMap;
    }


    @Override
    public void generateStrategy(final List<Metric> values) {
        if (!values.isEmpty() && buckets > 0) {

            // Array that holds metric values
            double[] arrayV = values.stream().mapToDouble(m -> Double.parseDouble(m.value())).sorted().toArray();

            // Contains the percentiles that represent the edge of a bucket
            List<Double> percentiles =  Stream
                    .iterate(stepSize + (100 % buckets), n -> n + stepSize)
                    .limit(buckets)
                    .map(p -> StatUtils.percentile(arrayV, p))
                    .toList();

            int bucket = 0;
            //Put each element on the appropriate bucket
            for (double element : arrayV) {
                if (element > percentiles.get(bucket)) {
                    bucket++;
                }
                valueBucketMap.put(String.valueOf((int) element), (bucket + 1));
            }
        } else {
            throw new RuntimeException("Trying to generate a mapper with an empty set or with zero buckets");
        }

    }

    @Override
    public Double mapValue(final Metric metric) {
        Integer returnVal = valueBucketMap.get(metric.value());

        if (returnVal == null) {
            throw new RuntimeException("Tried to map a missing original value: " + metric.value());
        }

        // To avoid having a return value higher than the max height
        return returnVal * scalingFactor;
    }

}
