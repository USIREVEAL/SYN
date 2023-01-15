package com.usi.ch.syn.core.model.analysis;

import com.usi.ch.syn.core.model.history.ProjectHistory;

@FunctionalInterface
public interface AnalysisResults {
    ProjectHistory getProjectHistory();
}
