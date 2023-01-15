package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;

import java.lang.reflect.Type;
import java.util.List;

public class ProjectAnalysisResultDeserializer implements JsonDeserializer<ProjectAnalysisResult> {


    @Override
    public ProjectAnalysisResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();

        Project project = context.deserialize(jsonObj.get("project"), Project.class);
        boolean analysisCompleted = jsonObj.get("analysisCompleted").getAsBoolean();
        String firstCommit = jsonObj.get("firstCommit").getAsString();
        String lastCommit = jsonObj.get("lastCommit").getAsString();
        long timestamp = jsonObj.get("timestamp").getAsLong();

        /* DESERIALIZE FILE HISTORY */
        List<FileHistory> fileHistories = context.deserialize(jsonObj.get("fileHistories"), TypeToken.getParameterized(List.class, FileHistory.class).getType());

        /* DESERIALIZE FILE VERSIONS */
        List<FileVersion> fileVersions = context.deserialize(jsonObj.get("fileVersions"), TypeToken.getParameterized(List.class, FileVersion.class).getType());

        /* DESERIALIZE PROJECT VERSION */
        List<ProjectVersion> projectVersions = context.deserialize(jsonObj.get("projectVersions"), TypeToken.getParameterized(List.class, ProjectVersion.class).getType());

        return new ProjectAnalysisResult(project, analysisCompleted, timestamp, firstCommit, lastCommit, projectVersions, fileHistories, fileVersions);
    }
}
