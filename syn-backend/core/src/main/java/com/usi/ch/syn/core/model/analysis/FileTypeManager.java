package com.usi.ch.syn.core.model.analysis;

import com.usi.ch.syn.core.metric.FileMetricCalculator;
import com.usi.ch.syn.core.metric.FileMetricFunctions;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.history.FileHistory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FileTypeManager {

    public static Map<String, List<FileMetricCalculator>> typeMetricMap = new HashMap<>();

    static {
        typeMetricMap.put("TEXT", List.of(FileMetricFunctions.SIZE_METRIC_CALCULATOR, FileMetricFunctions.LOC_METRIC_CALCULATOR));
        typeMetricMap.put("BINARY", List.of(FileMetricFunctions.SIZE_METRIC_CALCULATOR));
        typeMetricMap.put("JAVA", List.of(FileMetricFunctions.SIZE_METRIC_CALCULATOR, FileMetricFunctions.SLOC_METRIC_CALCULATOR, FileMetricFunctions.LOC_METRIC_CALCULATOR));
    }

    public static List<FileMetricCalculator> getMetricsToBeComputed(FileHistory fileHistory) {
        return fileHistory.getFileTypes().stream().flatMap(tag -> typeMetricMap.getOrDefault(tag, List.of()).stream()).distinct().toList();
    }

    public static void setFileHistoryTags(FileHistory fileHistory, Project project) {
        String path = project.getPath() + "/" + fileHistory.getPath();

        if (isBinaryFile(path)) {
            fileHistory.getFileTypes().add("BINARY");
        } else {
            fileHistory.getFileTypes().add("TEXT");
        }

        if (fileHistory.getName().contains(".")) {
            String extension = fileHistory.getName().substring(fileHistory.getName().lastIndexOf(".") + 1);
            fileHistory.getFileTypes().add(extension.toUpperCase(Locale.ROOT));
        }
    }

    private static boolean isBinaryFile(String filePath) {
        try {
            Process process = Runtime.getRuntime().exec("file " + filePath);
            BufferedReader ba = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return !ba.readLine().contains("text");
        } catch (Exception ignored) { }

        return false;
    }

}
