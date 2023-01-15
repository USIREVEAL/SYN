package com.usi.ch.syn.core.model.analysis;

import java.util.concurrent.BlockingQueue;

public abstract class Analyzer {

    public abstract ProjectAnalysisResult runAnalysis(AnalysisWorkDescriptor analysisWorkDescriptor);
    public abstract ProjectAnalysisResult runAnalysis(AnalysisWorkDescriptor analysisWorkDescriptor, BlockingQueue<AnalysisStatus> analysisStatusQueue);
}
