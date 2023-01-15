package com.usi.ch.syn.core.metric;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

public class FileMetricFunctions {

    public static final FileMetricCalculator SIZE_METRIC_CALCULATOR = new FileMetricCalculator("SIZE", file -> {
        long fileBytes = file.length();
        return String.valueOf(fileBytes);
    });


    public static final FileMetricCalculator LINES_METRIC_CALCULATOR = new FileMetricCalculator("LINES", file -> {
        int lines = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(file.toPath());
            while (reader.readLine() != null) lines++;
            reader.close();
        } catch (IOException e) {
            lines = 0;
        }
        return String.valueOf(lines);
    });

    public static final FileMetricCalculator SLOC_METRIC_CALCULATOR = new FileMetricCalculator("SLOC", file -> {
        int lines = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(file.toPath());
            String line;
            while ((line = reader.readLine()) != null) {
                if (line != "")
                    lines++;
            }
            reader.close();
        } catch (IOException e) {
            lines = 0;
        }
        return String.valueOf(lines);
    });

    public static final FileMetricCalculator LOC_METRIC_CALCULATOR = new FileMetricCalculator("LOC", file -> {
        int lines = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(file.toPath());
            String line;
            while ((line = reader.readLine()) != null) {
                if (line != "")
                    lines++;
            }
            reader.close();
        } catch (IOException e) {
            lines = 0;
        }
        return String.valueOf(lines);
    });
}
