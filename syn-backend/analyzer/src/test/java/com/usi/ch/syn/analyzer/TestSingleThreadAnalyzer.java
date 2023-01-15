package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.core.model.analysis.AnalysisStatus;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.Analyzer;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.model.project.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestSingleThreadAnalyzer {

    private static final Logger logger = LogManager.getLogger(TestSingleThreadAnalyzer.class);
    final static String SMALL_PROJECT_NAME = "JetUML";
    final static String SMALL_PROJECT_PATH = "https://github.com/prmr/JetUML.git";
    private static final ProjectFactory projectFactory = ProjectFactory.getInstance();
    private static Project project;

    @BeforeAll
    public static void initializeProject() {
        project = projectFactory.createProject(SMALL_PROJECT_NAME, SMALL_PROJECT_PATH);
    }

    @AfterAll
    public static void removeProject() {
        projectFactory.deleteProject(project.getId());
    }


    @Test
    public void testWithSmallRepo() {
//        ProjectAnalysisResult analysisResult = new ProjectAnalyzer().analyzeProject(project);
//        analysisResult = new ProjectAnalyzerJoiner().joinAnalysisResults(analysisResult);
//        AnalysisValidator analysisValidator = new AnalysisValidator();
//        analysisValidator.isAnalysisValid(analysisResult);
    }
//
//    @Test
//    public void testWithMediumRepo() {
//        ProjectAnalysisResult analysisResult = new ProjectAnalyzer().analyzeProject(project);
//        analysisResult = new ProjectAnalyzerJoiner().joinAnalysisResults(analysisResult);
//
//        AnalysisValidator analysisValidator = new AnalysisValidator();
//        projectFactory.storeProjectAnalysis(analysisResult);
//
//        analysisValidator.isAnalysisValid(analysisResult);
//    }
//
//    @Test
//    public void testWithMedium2Repo() throws GitAPIException, IOException {
//        ProjectAnalysisResult analysisResult = new ProjectAnalyzer().analyzeProject(project);
//        analysisResult = new ProjectAnalyzerJoiner().joinAnalysisResults(analysisResult);
//    }

    @Test
    public void testAnalysisStatus() {
        List<AnalysisWorkDescriptor> analysisWorkDescriptor = new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, 1);
        BlockingQueue<AnalysisStatus> analysisStatuses = new LinkedBlockingQueue<>();

        Thread status = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    AnalysisStatus analysisStatus = analysisStatuses.take();
                    System.out.println(analysisStatus);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        status.start();
        System.out.println("Start");
        ProjectAnalysisResult projectAnalysisResult = new ProjectAnalyzer().runAnalysis(analysisWorkDescriptor.get(0), analysisStatuses);

        status.interrupt();


    }



}
