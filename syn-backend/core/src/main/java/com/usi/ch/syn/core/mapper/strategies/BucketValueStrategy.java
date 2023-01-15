package com.usi.ch.syn.core.mapper.strategies;

import com.usi.ch.syn.core.mapper.MapperStrategy;
import com.usi.ch.syn.core.model.Metric;

import java.util.List;
import java.util.Map;

/**
 * This mapper uses the BucketCountStrategy to create buckets, but it ensures that same metrics values are considered once.
 */
public class BucketValueStrategy implements MapperStrategy<Metric, Double> {

    BucketCountStrategy bucketCountStrategy;
    public BucketValueStrategy(int buckets, Integer maxValue) {
        bucketCountStrategy = new BucketCountStrategy(buckets, maxValue);
    }


    @Override
    public void generateStrategy(final List<Metric> values) {
        bucketCountStrategy.generateStrategy(values.stream().distinct().toList());
    }

    @Override
    public Double mapValue(final Metric x) {
        return bucketCountStrategy.mapValue(x);
    }

    public Map<String, Integer> getValueBucketMap() {
        return bucketCountStrategy.getValueBucketMap();
    }

}
