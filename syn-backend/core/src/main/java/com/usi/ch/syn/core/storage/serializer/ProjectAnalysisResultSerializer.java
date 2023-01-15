package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;

import java.lang.reflect.Type;


public class ProjectAnalysisResultSerializer implements JsonSerializer<ProjectAnalysisResult> {


    @Override
    public JsonElement serialize(ProjectAnalysisResult projectAnalysisResult, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("project", context.serialize(projectAnalysisResult.getProject()));
        jsonObject.add("analysisCompleted", context.serialize(projectAnalysisResult.isAnalysisCompleted()));
        jsonObject.add("firstCommit", context.serialize(projectAnalysisResult.getFirstCommit()));
        jsonObject.add("lastCommit", context.serialize(projectAnalysisResult.getLastCommit()));
        jsonObject.add("timestamp", context.serialize(projectAnalysisResult.getTimestamp()));
        jsonObject.add("projectVersions", context.serialize(projectAnalysisResult.getProjectVersions().stream().sorted().toList()));
        jsonObject.add("fileHistories", context.serialize(projectAnalysisResult.getFileHistories().stream().sorted().toList()));
        jsonObject.add("fileVersions", context.serialize(projectAnalysisResult.getFileVersions().stream().sorted().toList()));
        return jsonObject;
    }

}

