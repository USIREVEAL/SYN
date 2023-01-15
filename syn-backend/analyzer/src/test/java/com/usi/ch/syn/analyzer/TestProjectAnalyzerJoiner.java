package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.model.project.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestProjectAnalyzerJoiner {

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
    public void testJoinAnalysisResult() {
        AnalysisWorkDescriptor analysisWorkDescriptor = new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, 1).get(0);
        ProjectAnalysisResult analysisResultSingleShot =  new ProjectAnalyzer().runAnalysis(analysisWorkDescriptor);
        ProjectAnalysisResult completeAnalysis =  new ProjectAnalyzerJoiner().joinAnalysisResults(analysisResultSingleShot);

        List<Integer> chunkSizeList = List.of(5);
        for (Integer chunkSize : chunkSizeList) {
            List<ProjectAnalysisResult> projectAnalysisResultList = new ArrayList<>();
            new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, chunkSize)
                    .stream()
                    .map(awd -> new ProjectAnalyzer().runAnalysis(awd))
                    .forEach(projectAnalysisResultList::add);

            ProjectAnalysisResult joinedAnalysis = new ProjectAnalyzerJoiner().joinAnalysisResults(projectAnalysisResultList.toArray(new ProjectAnalysisResult[chunkSize]));

            assertAll("Analysis results equals with " + chunkSize + " analysis",
                    () -> assertEquals(completeAnalysis.getProjectVersions().size(), joinedAnalysis.getProjectVersions().size()),
                    () -> assertEquals(completeAnalysis.getFileHistories().size(), joinedAnalysis.getFileHistories().size()),
                    () -> assertEquals(completeAnalysis.getFileVersions().size(), joinedAnalysis.getFileVersions().size())
            );
        }
    }



}
