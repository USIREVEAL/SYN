package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;

import static com.usi.ch.syn.core.storage.serializer.EntitySerializer.serializeCodeEntityFields;

public class FileHistorySerializer implements JsonSerializer<FileHistory> {
    private static final Logger logger = LoggerFactory.getLogger(FileHistorySerializer.class);

//    public static void serializeFileHistories(Project project) {
//        File fileHistoryFolder = getFileHistoryFolderPath(project).toFile();
//        GsonBuilder gson = new GsonBuilder();
//
//        FileHistorySerializer entitySerializer= new FileHistorySerializer();
//        gson.registerTypeAdapter(FileHistory.class, entitySerializer);
//        gson.setPrettyPrinting();
//
//        if (fileHistoryFolder.mkdir()) {
//            List<FileHistory> fileHistories = project.getProjectHistory().getFileHistories();
//
//            Path entityPath = getFileHistoryPath(project);
//
//            try (FileWriter writer = new FileWriter(entityPath.toFile());
//                 BufferedWriter bw = new BufferedWriter(writer)) {
//                bw.write(gson.create().toJson(fileHistories));
//            }catch(IOException ex){
//                logger.error("Unable to write to file {}", entityPath);
//            }
//        }
//    }

    @Override
    public JsonElement serialize(FileHistory entity, Type typeOfSrc, JsonSerializationContext context) {
        // Represents a entity object
        JsonObject jsonObject = new JsonObject();
        serializeCodeEntityFields(entity, jsonObject);

        jsonObject.addProperty("name", entity.getName());
        jsonObject.addProperty("path", entity.getPath());

        JsonArray versionJSONArray = new JsonArray();
        entity.getFileVersions().stream()
            .map(FileVersion::getId)
            .forEach(versionJSONArray::add);
        jsonObject.add("fileVersions", versionJSONArray);

        JsonArray historyChangesJSONArray = new JsonArray();
        entity.getAliases().forEach(historyChangesJSONArray::add);
        jsonObject.add("aliases", historyChangesJSONArray);

        JsonArray tagsJSONArray = new JsonArray();
        entity.getFileTypes().forEach(tagsJSONArray::add);
        jsonObject.add("fileTypes", tagsJSONArray);


        return jsonObject;
    }

}
