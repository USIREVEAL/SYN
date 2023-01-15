package com.usi.ch.syn.core.storage;

import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.utils.ProjectFileIdentifier;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnalysisWorkerTest {

    final String SMALL_PROJECT_NAME = "JetUML";
    final String SMALL_PROJECT_PATH = "https://github.com/prmr/JetUML.git";

    @Test
    public void testStorageAnalysisWorker() throws IOException, StorageException {
        Project project = ProjectFactory.getInstance().createProject(SMALL_PROJECT_NAME, SMALL_PROJECT_PATH);
        List<String> fakeStringList = List.of("hash1", "hash2", "hash3");
        AnalysisWorkDescriptor analysisWorkDescriptor = new AnalysisWorkDescriptor(project, fakeStringList);
        Path tempFile = Files.createTempFile("tmpAnalysis", "json");
        StorageHelper.storeAnalysisWorkDescriptor(analysisWorkDescriptor, tempFile.toFile());
        AnalysisWorkDescriptor loadedAnalysis = StorageHelper.loadAnalysisWorkDescriptor(tempFile);
        assertEquals(analysisWorkDescriptor, loadedAnalysis);
    }

}
