package com.usi.ch.syn.analyzer.jgit;

import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitException;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestJGitProject {

    @Test
    public void testCommitSize() {
        Project project = ProjectFactory.getInstance().createProject("JetUML", "https://github.com/prmr/JetUML.git");
        JGitProject jGitProject = new JGitProject(project);
        List<GitCommit> commitList = jGitProject.getCommits();
        assertTrue(commitList.size() > 1000);
    }

    @Test
    public void testCommitSizeRange() {
        Project project = ProjectFactory.getInstance().createProject("JetUML", "https://github.com/prmr/JetUML.git");
        JGitProject jGitProject = new JGitProject(project);
        List<GitCommit> commitList = jGitProject.getCommits("ad57c50b2ab897a4cc1f7c2d7ce67489d1769f86", "0b16c93f0bca096a14f2921455b70bd1517dfb91");
        assertEquals(8, commitList.size());
    }

    @Test
    public void testDirtyCheckout() throws GitException {
        Project project = ProjectFactory.getInstance().createProject("JetUML", "https://github.com/prmr/JetUML.git");
        JGitProject jGitProject = new JGitProject(project);
        List<GitCommit> gitCommits = jGitProject.getCommits();
        gitCommits.get(0).checkout();
        try {
            Path filePath = Files.list(Path.of(project.getPath())).findAny().get();
            Files.write(filePath, List.of("Lol"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gitCommits.get(10).checkout();
    }
}
