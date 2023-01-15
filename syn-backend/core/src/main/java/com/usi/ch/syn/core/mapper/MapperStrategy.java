package com.usi.ch.syn.core.mapper;

import java.util.List;

/**
 *
 * @param <F> From type
 * @param <T> To type
 */
public interface MapperStrategy<F, T> {

    void generateStrategy(final List<F> values) throws MapperException;

    T mapValue(final F value);
}
