package com.usi.ch.syn.analyzer;

import com.usi.ch.syn.analyzer.jgit.JGitProject;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitException;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAnalysisWorkerDescriptorFactory {

    final static String SMALL_PROJECT_NAME = "JetUML";
    final static String SMALL_PROJECT_PATH = "https://github.com/prmr/JetUML.git";
    private static final Logger logger = LogManager.getLogger(TestAnalysisWorkerDescriptorFactory.class);
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
    public void testSplitAnalysis() throws GitException {
        List<Integer> chunkSizeList = List.of(1, 2, 3, 5, 7, 10, 11, 50, 100, 1000);
        for (Integer chunkSize : chunkSizeList) {
            List<AnalysisWorkDescriptor> analyzeDescriptorList = new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, chunkSize);
            assertEquals(chunkSize, analyzeDescriptorList.size());

            GitProject gitProject = new JGitProject(project);
            List<GitCommit> commitList = gitProject.getCommits();

            int splitAnalysisCommits = 0;
            int commitListIndex = 0;
            for (AnalysisWorkDescriptor analysisWorkDescriptor : analyzeDescriptorList) {
                assertEquals(commitList.get(commitListIndex).getHash(), analysisWorkDescriptor.commits().get(0));
                int commitsInsideDescriptor = analysisWorkDescriptor.commits().size();
                assertEquals(commitList.get(commitListIndex + (commitsInsideDescriptor - 1)).getHash(), analysisWorkDescriptor.commits().get((commitsInsideDescriptor - 1)));
                commitListIndex += commitsInsideDescriptor;

                List<GitCommit> partialCommitList = gitProject.getCommits(analysisWorkDescriptor);

                if (!(analysisWorkDescriptor.commits().size() == partialCommitList.size())) {
                    int index = 0;
                    GitCommit partialCommitListCommit;
                    String analyzerDescriptorHash;
                    do {
                        partialCommitListCommit = partialCommitList.get(index);
                        analyzerDescriptorHash = analysisWorkDescriptor.commits().get(index);
                        index++;
                    } while (index < analysisWorkDescriptor.commits().size() && partialCommitListCommit.getHash().equals(analyzerDescriptorHash));

                    System.out.println("");

                }

                assertEquals(analysisWorkDescriptor.commits().size(), partialCommitList.size());
                splitAnalysisCommits += partialCommitList.size();
            }

            assertEquals(commitList.size(), splitAnalysisCommits);

        }
    }


}
