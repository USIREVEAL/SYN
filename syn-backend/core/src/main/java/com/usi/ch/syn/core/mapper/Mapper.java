package com.usi.ch.syn.core.mapper;

import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;

/**
 * This class represents a generic mapper for any entity inside a ProjectHistory.
 * @param <F> The type of the value before the mapping was done
 * @param <T>The type of the value after the mapping was done
 *
 * e.g. Mapper<String, Double> maps Strings to Doubles
 */

@Getter
public class Mapper<F, T> {

    // Needed to retrieve information about the frequency of a value
    private List<F> originalValues;

    private Map<F, T> mappedValues = new HashMap<>();


    protected <U> Mapper(ProjectHistory projectHistory, Function<ProjectHistory, List<? extends U>> entityRetriever, Function<U, F> valueRetriever, MapperStrategy<F, T> valueMapper) throws MapperException {
        List<? extends U> entitiesToBeMapped = entityRetriever.apply(projectHistory);
        originalValues = entitiesToBeMapped.stream().map(ele -> {
            try {
                return valueRetriever.apply(ele);
            } catch (NoSuchElementException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        valueMapper.generateStrategy(originalValues);
        originalValues.forEach(value -> mappedValues.computeIfAbsent(value, valueMapper::mapValue));
    }

    public static <R, F> Mapper<R, F> createOnProjectVersion(ProjectHistory projectHistory, Function<ProjectVersion, R> valueRetriever, MapperStrategy<R, F> valueMapper)  throws MapperException {
        Function<ProjectHistory, List<? extends ProjectVersion>> entityRetriever = ProjectHistory::getProjectVersions;
        return new Mapper<R, F>(projectHistory, entityRetriever, valueRetriever, valueMapper);
    }

    public static <R, F> Mapper<R, F> createOnFileVersion(ProjectHistory projectHistory, Function<FileVersion, R> valueRetriever, MapperStrategy<R, F> valueMapper)  throws MapperException {
        Function<ProjectHistory, List<? extends FileVersion>> entityRetriever = ProjectHistory::getAllFileVersions;
        return new Mapper<>(projectHistory, entityRetriever, valueRetriever, valueMapper);
    }

    public static <R, F> Mapper<R, F> createOnFileHistory(ProjectHistory projectHistory, Function<FileHistory, R> valueRetriever, MapperStrategy<R, F> valueMapper)  throws MapperException {
        Function<ProjectHistory, List<? extends FileHistory>> entityRetriever = ProjectHistory::getFileHistories;
        return new Mapper<>(projectHistory, entityRetriever, valueRetriever, valueMapper);
    }

    public Map<F, T> getDictionary() {
        return mappedValues;
    }

    public T mapValue(F originalValue) {
        return mappedValues.get(originalValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mapper<?, ?> mapper = (Mapper<?, ?>) o;
        return Objects.equals(mappedValues, mapper.mappedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mappedValues);
    }
}
