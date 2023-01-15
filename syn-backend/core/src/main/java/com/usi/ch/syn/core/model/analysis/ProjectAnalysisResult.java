package com.usi.ch.syn.core.model.analysis;

import com.usi.ch.syn.core.model.EntityIdTranslator;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
public class ProjectAnalysisResult {
    private final Project project;
    private boolean analysisCompleted;

    private long timestamp;

    @Setter
    private String firstCommit = "", lastCommit = "";

    private final List<ProjectVersion> projectVersions;
    private final List<FileHistory> fileHistories;
    private final List<FileVersion> fileVersions;

    public static ProjectAnalysisResult buildPartialAnalysisResult(Project project) {
        ProjectAnalysisResult projectAnalysisResult = new ProjectAnalysisResult(project);
        projectAnalysisResult.analysisCompleted = false;
        projectAnalysisResult.timestamp = Instant.now().getEpochSecond();

        return projectAnalysisResult;
    }

    public static ProjectAnalysisResult buildCompleteAnalysisResult(Project project, String firstCommit, String lastCommit) {
        ProjectAnalysisResult projectAnalysisResult = new ProjectAnalysisResult(project);
        projectAnalysisResult.analysisCompleted = true;
        projectAnalysisResult.firstCommit = firstCommit;
        projectAnalysisResult.lastCommit = lastCommit;
        projectAnalysisResult.timestamp = Instant.now().getEpochSecond();

        return projectAnalysisResult;
    }

    public ProjectAnalysisResult(Project project) {
        this.project = project;
        this.projectVersions = new ArrayList<>();
        this.fileHistories = new ArrayList<>();
        this.fileVersions = new ArrayList<>();
    }
    public ProjectAnalysisResult(Project project, boolean analysisCompleted, long timestamp, String firstCommit, String lastCommit,  List<ProjectVersion> projectVersions, List<FileHistory> fileHistories, List<FileVersion> fileVersions) {
        this.project = project;
        this.analysisCompleted = analysisCompleted;
        this.timestamp = timestamp;
        this.firstCommit = firstCommit;
        this.lastCommit = lastCommit;
        this.projectVersions = Collections.unmodifiableList(projectVersions);
        this.fileHistories = Collections.unmodifiableList(fileHistories);
        this.fileVersions = Collections.unmodifiableList(fileVersions);
    }

    public FileVersion createNewFileVersion(Change change, FileHistory fileHistory) {
        FileVersion fileVersion = new FileVersion(
                EntityIdTranslator.generateEntityId(FileVersion.class, fileVersions.size() + 1),
                change,
                fileHistory
        );
        fileVersions.add(fileVersion);
        return fileVersion;
    }

    public FileHistory createNewFileHistory(String fileName, String filePath) {
        FileHistory fileHistory = new FileHistory(
                EntityIdTranslator.generateEntityId(FileHistory.class, fileHistories.size() + 1),
                fileName, filePath);
        fileHistories.add(fileHistory);

        FileTypeManager.setFileHistoryTags(fileHistory, project);
        return fileHistory;
    }

    public ProjectVersion createNewProjectVersion(long timestamp, String commitHash, String commitMessage) {
        ProjectVersion projectVersion = new ProjectVersion(
                EntityIdTranslator.generateEntityId(ProjectVersion.class, projectVersions.size() + 1),
                timestamp, commitHash, commitMessage);
        projectVersions.add(projectVersion);
        return projectVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectAnalysisResult)) return false;
        ProjectAnalysisResult that = (ProjectAnalysisResult) o;
        return getFirstCommit().equals(that.getFirstCommit()) && getLastCommit().equals(that.getLastCommit()) && Objects.equals(getProjectVersions(), that.getProjectVersions()) && Objects.equals(getFileHistories(), that.getFileHistories()) && Objects.equals(getFileVersions(), that.getFileVersions()) && getProject().equals(that.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstCommit(), getLastCommit(), getProjectVersions(), getFileHistories(), getFileVersions(), getProject());
    }

}
