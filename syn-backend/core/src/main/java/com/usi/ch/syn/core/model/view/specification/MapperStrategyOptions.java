package com.usi.ch.syn.core.model.view.specification;


import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MapperStrategyOptions {
    private int buckets;
    private int maxHeight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapperStrategyOptions)) return false;
        MapperStrategyOptions that = (MapperStrategyOptions) o;
        return getBuckets() == that.getBuckets() && getMaxHeight() == that.getMaxHeight();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBuckets(), getMaxHeight());
    }
}