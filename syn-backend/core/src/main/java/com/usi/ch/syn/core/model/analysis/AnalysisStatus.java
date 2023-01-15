package com.usi.ch.syn.core.model.analysis;

public record AnalysisStatus(Thread thread, int commitsAnalyzed, int remainingCommits, int discoveredFileHistories, int discoveredFileVersions, String status) { }
