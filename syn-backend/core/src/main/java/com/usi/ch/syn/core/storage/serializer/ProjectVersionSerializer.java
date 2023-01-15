package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import static com.usi.ch.syn.core.storage.serializer.EntitySerializer.serializeCodeEntityFields;

public class ProjectVersionSerializer implements JsonSerializer<ProjectVersion> {
    private static final Logger logger = LoggerFactory.getLogger(ProjectVersionSerializer.class);

//    public static void serializeProjectVersions(Project project) {
//        File versionDir = getProjectVersionFolderPath(project).toFile();
//
//        GsonBuilder gson = new GsonBuilder();
//        gson.registerTypeAdapter(ProjectVersion.class, new ProjectVersionSerializer());
//        gson.setPrettyPrinting();
//
//        if (versionDir.mkdir()) {
//            List<ProjectVersion> projectVersions = project.getProjectHistory().getProjectVersions();
//            Path versionFilePath = getProjectVersionPath(project);
//
//            try (FileWriter writer = new FileWriter(versionFilePath.toFile());
//                 BufferedWriter bw = new BufferedWriter(writer)) {
//                bw.write(gson.create().toJson(projectVersions));
//            }catch(IOException ex){
//                logger.error("Unable to write to file {}", versionFilePath);
//            }
//        }
//    }

    @Override
    public JsonElement serialize(ProjectVersion version, Type typeOfSrc, JsonSerializationContext context) {
        // Create the version object
        JsonObject versionJSONObj = new JsonObject();

        serializeCodeEntityFields(version, versionJSONObj);
        if (version.getNext() != null)
            versionJSONObj.addProperty("next", version.getNext().getId());

        if (version.getPrevious() != null)
            versionJSONObj.addProperty("previous", version.getPrevious().getId());

        versionJSONObj.addProperty("commit", version.getCommitHash());
        versionJSONObj.addProperty("timestamp", version.getTimestamp());
        versionJSONObj.addProperty("message", version.getCommitMessage());

        JsonArray fileVersionsArray = new JsonArray();
        version.getFileVersions().stream().map(FileVersion::getId).forEach(fileVersionsArray::add);
        versionJSONObj.add("fileVersions", fileVersionsArray);

        return versionJSONObj;
    }


}
