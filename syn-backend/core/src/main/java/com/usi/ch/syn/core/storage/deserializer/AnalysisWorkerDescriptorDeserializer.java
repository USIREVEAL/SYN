package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.LocalProject;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.core.model.version.ProjectVersion;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AnalysisWorkerDescriptorDeserializer implements JsonDeserializer<AnalysisWorkDescriptor> {

    @Override
    public AnalysisWorkDescriptor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Project project = context.deserialize(json.getAsJsonObject().get("project"), Project.class);
        List<String> hashList = context.deserialize(json.getAsJsonObject().get("commits"), TypeToken.getParameterized(List.class, String.class).getType());
        return new AnalysisWorkDescriptor(project, hashList);
    }

}
