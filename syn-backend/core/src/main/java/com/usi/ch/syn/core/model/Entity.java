package com.usi.ch.syn.core.model;

import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

/**
 * This class represents an Entity of the SYN model.
 * <p>
 * Each entity must have a unique identifier within the scope of a project.
 * This class contains a map of entities in which the id will also carry information about the class.
 * <p>
 * Each id has the following format xxyyyyyyyy where xx represents the class
 * identifier and yyyyyyyy represents the entity itself.
 * e.g. the 104th fileVersion might have the following id 0400000104 where 04 identifies the FileVersion class and
 * 00000104 identifies the object itself.
 *
 * xyyyyyyyy
 * 400000000
 */

@Getter
public abstract class Entity implements Comparable<Entity> {

    protected final int id;

    public Entity(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Entity o) {
        return Comparator.comparingLong(Entity::getId).compare(this, o);
    }

    ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
