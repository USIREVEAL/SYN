package com.usi.ch.syn.core.model;

import lombok.Getter;

@Getter
public abstract class EntityFigure {
    private final Entity entity;

    public EntityFigure(Entity entity) {
        this.entity = entity;
    }
}
