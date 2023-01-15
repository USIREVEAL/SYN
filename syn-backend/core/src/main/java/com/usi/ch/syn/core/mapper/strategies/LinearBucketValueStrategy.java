package com.usi.ch.syn.core.mapper.strategies;

import com.usi.ch.syn.core.mapper.MapperStrategy;
import com.usi.ch.syn.core.model.Metric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This strategy maps the value of a metric to a double.
 *  To do this mapping the value is first bucketized and then normalized within the bucket.
 *  As a result, different metric values inside the same bucket have different values.
 */
public class LinearBucketValueStrategy implements MapperStrategy<Metric, Double> {

    BucketValueStrategy bucketValueStrategy;
    public Map<String, Double> originalMappedValueMap = new HashMap<>();
    double scalingFactor;


    public LinearBucketValueStrategy(int buckets, Integer maxValue) {
        bucketValueStrategy = new BucketValueStrategy(buckets, maxValue);

        scalingFactor = maxValue <= 0 ? 1 : (double) maxValue / (buckets + 1);
    }


    @Override
    public void generateStrategy(final List<Metric> values) {
        bucketValueStrategy.generateStrategy( values.stream().distinct().toList());
        // maps each metric value to the bucket
        Map<String, Integer> getValueBucketMap = bucketValueStrategy.getValueBucketMap();

        // maps each bucket to its maximum value
        Map<Integer, Double> maxBucketValueMap = new HashMap<>();

        getValueBucketMap.forEach((strVal, bucket) -> {
            double doubleVal = Double.parseDouble(strVal);
            double bucketMaxVal = maxBucketValueMap.getOrDefault(bucket, -1.0);
            if (doubleVal > bucketMaxVal) {
                maxBucketValueMap.put(bucket, doubleVal);
            }
        });

        getValueBucketMap.forEach((strVal, bucket) -> {
            int intVal = Integer.parseInt(strVal);
            double linearValueInsideBucket = 1;
            if (maxBucketValueMap.containsKey(bucket)) {
                linearValueInsideBucket = intVal / maxBucketValueMap.get(bucket);
            }

            originalMappedValueMap.put(strVal, bucket + linearValueInsideBucket);
        });
    }

    @Override
    public Double mapValue(final Metric x) {
        Double returnVal = originalMappedValueMap.get(x.value());

        if (returnVal == null) {
            throw new RuntimeException("Tried to map a missing original value: " + x);
        }

        return returnVal * scalingFactor;
    }



}
