package com.usi.ch.syn.core.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents an entity that may hold some metrics.
 */

public abstract class CodeEntity extends Entity {

    @Getter
    protected List<Metric> metrics = new ArrayList<>();

    protected CodeEntity(int id) {
        super(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeEntity)) return false;
        if (!super.equals(o)) return false;
        CodeEntity that = (CodeEntity) o;
        return Objects.equals(getMetrics(), that.getMetrics());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMetrics());
    }
}
