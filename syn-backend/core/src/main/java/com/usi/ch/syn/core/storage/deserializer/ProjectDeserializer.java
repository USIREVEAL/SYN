package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.project.LocalProject;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectDeserializer implements JsonDeserializer<Project> {

    public static Project getProjectFromDescriptor(Path projectDescriptorPath) {
        GsonBuilder gson = new GsonBuilder();

        gson.registerTypeAdapter(Project.class, new ProjectDeserializer());
        gson.setPrettyPrinting();

        try {
            String json = String.join("", Files.readAllLines(projectDescriptorPath));
            return gson.create().fromJson(json, Project.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Project deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Project project;
        JsonObject jsonObject = json.getAsJsonObject();

        if (json.getAsJsonObject().has("projectURL")) {
            project = RemoteProject.create(
                    jsonObject.get("id").getAsInt(),
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("path").getAsString(),
                    jsonObject.get("projectURL").getAsString());

        } else {
            project = LocalProject.create(
                    jsonObject.get("id").getAsInt(),
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("path").getAsString());
        }

//        project.setLastAnalysisTs(jsonObject.get("lastAnalysisTs").getAsLong());
        EntityDeserializer.deserializeCodeEntityFields(project, jsonObject);

        return project;
    }

}
