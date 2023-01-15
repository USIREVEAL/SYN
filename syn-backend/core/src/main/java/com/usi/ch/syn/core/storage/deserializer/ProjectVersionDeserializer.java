package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ProjectVersionDeserializer implements JsonDeserializer<ProjectVersion> {

    private final Map<Integer, FileVersion> fileVersionMap;
    private final Map<Integer, ProjectVersion> projectVersionMap;


    public ProjectVersionDeserializer(Map<Integer, FileVersion> fileVersionMap) {
        this.fileVersionMap = fileVersionMap;
        projectVersionMap = new HashMap<>();
    }

//    public List<ProjectVersion> deserializeProjectVersions(Project project) {
//        GsonBuilder gson = new GsonBuilder();
//
//        gson.registerTypeAdapter(ProjectVersion.class, this);
//        gson.setPrettyPrinting();
//
////        Path versionPath = getProjectVersionPath(project);
//
//        try {
//            String json = String.join("", Files.readAllLines(versionPath));
//            return gson.create().fromJson(json, TypeToken.getParameterized(List.class, ProjectVersion.class).getType());
//        } catch (IOException e) {
//            return null;
//        }
//    }

    @Override
    public ProjectVersion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObj = json.getAsJsonObject();

        int projectVersionId = jsonObj.get("id").getAsInt();

        ProjectVersion version = new ProjectVersion(
            jsonObj.get("id").getAsInt(),
            jsonObj.get("timestamp").getAsInt(),
            jsonObj.get("commit").getAsString(),
            jsonObj.get("message").getAsString()
        );


        jsonObj.getAsJsonArray("fileVersions").forEach(jsonElement -> {
            int fileVersionId = jsonElement.getAsInt();
            if (fileVersionMap.containsKey(fileVersionId)) {
                FileVersion fileVersion = fileVersionMap.get(fileVersionId);
                version.addFileVersion(fileVersion);
            }
        });

        if (jsonObj.has("previous")) {
            ProjectVersion previousVersion = projectVersionMap.get(jsonObj.get("previous").getAsInt());
            previousVersion.setNext(version);
            version.setPrevious(previousVersion);
        }

        EntityDeserializer.deserializeCodeEntityFields(version, jsonObj);
        projectVersionMap.putIfAbsent(projectVersionId, version);

        return version;
    }

}
