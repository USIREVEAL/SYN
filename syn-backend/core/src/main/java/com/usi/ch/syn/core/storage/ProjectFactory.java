package com.usi.ch.syn.core.storage;

import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.LocalProject;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.core.utils.Pair;
import com.usi.ch.syn.core.utils.ProjectFileIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.usi.ch.syn.core.storage.StorageHelper.storeProject;


public class ProjectFactory {

    Logger logger = LoggerFactory.getLogger(ProjectFactory.class);
    private final static ProjectFactory INSTANCE = new ProjectFactory();

    private  Map<Integer, Path> projectAnalysisPathMap;
    private final Map<Integer, Project> loadedAnalysisMap = new HashMap<>();
    
    public static ProjectFactory getInstance() { return INSTANCE; }

    private ProjectFactory() {
        refreshProjectPathMap();
    }

    public void refreshProjectPathMap() {
        this.projectAnalysisPathMap = StorageHelper.getProjectAnalysisPathMap();
    }
    
    public Project createProject(String name, String uri) {
        Project project;
        if (uri.contains("https://")) {
            project = RemoteProject.create(getNextAvailableID(), name, uri);
        } else if (Files.exists(Path.of(uri))) {
            project = LocalProject.create(getNextAvailableID(), name, uri);
        } else {
            throw new RuntimeException("Unable to add project " + name + " located at " + uri);
        }

        storeProject(ProjectAnalysisResult.buildPartialAnalysisResult(project));

        return project;
    }

    private int getNextAvailableID() {
        int i = 1;
        while (i < Integer.MAX_VALUE) {
            if (projectAnalysisPathMap.containsKey(i)) {
                i++;
            } else {
                return i;
            }
        }

        return -1;
    }

    /**
     * This method will load a project analysis from disk
     * @param projectAnalysisPath path of the json file representing the analysis of a project
     */
    public ProjectAnalysisResult loadProjectAnalysis(Path projectAnalysisPath) {
        logger.info("Loading project analysis from {}", projectAnalysisPath);
        long st = System.currentTimeMillis();
        ProjectAnalysisResult projectAnalysisResult = StorageHelper.loadProjectAnalysis(projectAnalysisPath);
        logger.info("Project analysis loaded in {}s", (System.currentTimeMillis() - st) / 1000);
        return projectAnalysisResult;
    }

    /**
     * This method can be used to retrieve a project analysis given the project id
     * @param projectID the id of the project that will be retrieved
     * @return an optional filled if the project with the given id exist, empty otherwise
     */
    public Optional<Project> getProject(int projectID) {

        if (!loadedAnalysisMap.containsKey(projectID)) {
            if (projectAnalysisPathMap.containsKey(projectID)) {
                ProjectAnalysisResult projectAnalysisResult = loadProjectAnalysis(projectAnalysisPathMap.get(projectID));
                Project project = projectAnalysisResult.getProject();
                loadedAnalysisMap.put(project.getId(), project);
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(loadedAnalysisMap.get(projectID));
    }

    /**
     * This method deletes the project from both memory and disk
     * @param projectID The id of the project that will be permanently deleted
     */
    public void deleteProject(int projectID) {
        if (projectAnalysisPathMap.containsKey(projectID)) {
            StorageHelper.deleteProjectAnalysis(projectAnalysisPathMap.get(projectID));
            projectAnalysisPathMap.remove(projectID);
        }
    }

    public void storeProjectAnalysis(ProjectAnalysisResult analysisResults) {
        storeProject(analysisResults);
    }

    public void storeProjectAnalysis(ProjectAnalysisResult analysisResults, File outputFile) {
        storeProject(analysisResults, outputFile);
    }

    public List<ProjectFileIdentifier> getListAvailableProjects() {
        return projectAnalysisPathMap
                .values()
                .stream()
                .map(p -> p.getFileName().toString())
                .map(s -> s.substring(0, s.indexOf('.')))
                .map(ProjectFileIdentifier::fromString)
                .toList();
    }

}
