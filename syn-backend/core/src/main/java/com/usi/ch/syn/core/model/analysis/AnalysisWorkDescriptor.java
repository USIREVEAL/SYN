package com.usi.ch.syn.core.model.analysis;

import com.usi.ch.syn.core.model.project.Project;

import java.util.List;
import java.util.Objects;

public record AnalysisWorkDescriptor(Project project, List<String> commits) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisWorkDescriptor)) return false;
        AnalysisWorkDescriptor that = (AnalysisWorkDescriptor) o;
        return Objects.equals(project, that.project) && Objects.equals(commits, that.commits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, commits);
    }
}
