package com.usi.ch.syn.core.metric;

import java.io.File;
import java.util.function.Function;

@FunctionalInterface
public interface FileMetricFunction extends Function<File, String> {
    String apply(File t);
}
