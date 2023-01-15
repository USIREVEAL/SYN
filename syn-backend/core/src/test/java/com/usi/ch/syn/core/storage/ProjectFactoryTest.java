package com.usi.ch.syn.core.storage;

import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.utils.ProjectFileIdentifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectFactoryTest {

    final String SMALL_PROJECT_NAME = "JetUML";
    final String SMALL_PROJECT_PATH = "https://github.com/prmr/JetUML.git";

    @Test
    public void testSerializationSmallProject() {
        ProjectFactory.getInstance().createProject(SMALL_PROJECT_NAME, SMALL_PROJECT_PATH);
    }

    @Test
    public void testDeserializationSmallProject() {
        ProjectFactory projectFactory = ProjectFactory.getInstance();
        Project project1 = projectFactory.createProject(SMALL_PROJECT_NAME, SMALL_PROJECT_PATH);
        projectFactory.refreshProjectPathMap();
        Project loadedProject1 =projectFactory.getProject(project1.getId()).get();
        assertEquals(loadedProject1, project1);
    }

    @Test
    public void testProjectList() {
        ProjectFactory projectFactory = ProjectFactory.getInstance();
        Project project1 = projectFactory.createProject(SMALL_PROJECT_NAME, SMALL_PROJECT_PATH);
        projectFactory.refreshProjectPathMap();
        List<ProjectFileIdentifier> projectList = ProjectFactory.getInstance().getListAvailableProjects();
        boolean found = false;
        for (ProjectFileIdentifier projectFileIdentifier : projectList) {
            if (projectFileIdentifier.id() == project1.getId() && projectFileIdentifier.name().equals(project1.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        ProjectFactory.getInstance().deleteProject(project1.getId());
    }
}
