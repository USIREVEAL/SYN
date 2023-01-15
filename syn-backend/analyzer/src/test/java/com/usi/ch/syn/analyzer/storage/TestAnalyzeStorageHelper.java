package com.usi.ch.syn.analyzer.storage;

import com.usi.ch.syn.analyzer.*;
import com.usi.ch.syn.core.model.analysis.AnalysisStatus;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestAnalyzeStorageHelper {
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
//        projectFactory.deleteProject(project.getId());
    }


    @Test
    public void testPartialAnalysisDeserialization() {
        List<AnalysisWorkDescriptor> analysisWorkDescriptor = new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, 1);

        ProjectAnalysisResult analysisResults = new ProjectAnalyzer().runAnalysis(analysisWorkDescriptor.get(0));
        projectFactory.storeProjectAnalysis(analysisResults);
        ProjectAnalysisResult loadedAnalysisResults =  projectFactory.loadProjectAnalysis(Config.getProjectAnalysisPath(analysisResults));
        assertEquals(analysisResults, loadedAnalysisResults);
    }
//
//    @Test
//    public void testFullAnalysisDeserialization() {
//        ProjectAnalysisResult partialAnalysis = new ProjectAnalyzer().analyzeProject(project, "", "");
//        ProjectAnalysisResult fullAnalysis = new ProjectAnalyzerJoiner().joinAnalysisResults(partialAnalysis);
//        projectFactory.storeProjectAnalysis(fullAnalysis);
//        ProjectAnalysisResult loadedFullAnalysis =  projectFactory.loadProjectAnalysis(Config.getProjectAnalysisPath(fullAnalysis));
//
//
//        AnalysisValidator.spotDifferencesBetweenAnalysis(fullAnalysis, loadedFullAnalysis);
//        assertEquals(fullAnalysis, loadedFullAnalysis);
//
//    }

}
