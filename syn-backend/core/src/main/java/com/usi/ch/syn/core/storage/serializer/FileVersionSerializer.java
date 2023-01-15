package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.version.FileVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class FileVersionSerializer implements JsonSerializer<FileVersion> {
    private static final Logger logger = LoggerFactory.getLogger(FileVersionSerializer.class);

//    public static void serializeFileVersions(Project project) {
//        File fileVersionDir = getFileVersionFolderPath(project).toFile();
//        GsonBuilder gson = new GsonBuilder();
//
//        gson.registerTypeAdapter(FileVersion.class, new FileVersionSerializer());
//        gson.registerTypeAdapter(Change.class, new ChangeSerializer());
//        gson.setPrettyPrinting();
//
//
//        if (fileVersionDir.mkdir()) {
//            List<FileVersion> fileVersions = project.getProjectHistory().getAllFileVersions();
//
//            Path fileVersionPath = getFileVersionPath(project);
//
//            try (FileWriter writer = new FileWriter(fileVersionPath.toFile());
//                 BufferedWriter bw = new BufferedWriter(writer)) {
//                bw.write(gson.create().toJson(fileVersions));
//            }catch(IOException ex){
//                logger.error("Unable to write to file {}", fileVersionPath);
//            }
//        }
//    }


    @Override
    public JsonElement serialize(FileVersion fileVersion, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject versionJSONObj = new JsonObject();

        EntitySerializer.serializeCodeEntityFields(fileVersion, versionJSONObj);
        if (fileVersion.getNext() != null)
            versionJSONObj.addProperty("next", fileVersion.getNext().getId());

        if (fileVersion.getPrevious() != null)
            versionJSONObj.addProperty("previous", fileVersion.getPrevious().getId());

        versionJSONObj.addProperty("projectVersion", fileVersion.getParentProjectVersion().getId());
        versionJSONObj.addProperty("fileHistory", fileVersion.getFileHistory().getId());

        versionJSONObj.add("change", context.serialize(fileVersion.getChange(), Change.class));

        return versionJSONObj;
    }



}
