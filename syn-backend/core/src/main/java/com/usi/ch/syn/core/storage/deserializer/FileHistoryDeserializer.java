package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.history.FileHistory;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.*;


public class FileHistoryDeserializer implements JsonDeserializer<FileHistory> {

    @Getter private final Map<Integer, FileHistory> fileHistoryMap = new HashMap<>();

//    public List<FileHistory> deserializeFileHistories(Project project) {
//        GsonBuilder gson = new GsonBuilder();
//
//        gson.registerTypeAdapter(FileHistory.class, this);
//        gson.setPrettyPrinting();
//
//        Path entityPath = getFileHistoryPath(project);
//
//        try {
//            String json = String.join("", Files.readAllLines(entityPath));
//            return gson.create().fromJson(json, TypeToken.getParameterized(List.class, FileHistory.class).getType());
//        } catch (IOException e) {
//            return null;
//        }
//    }

    @Override
    public FileHistory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject entityObj = json.getAsJsonObject();

        int fileHistoryId = entityObj.get("id").getAsInt();

        FileHistory fileHistory = new FileHistory(
                entityObj.get("id").getAsInt(),
                entityObj.get("name").getAsString(),
                entityObj.get("path").getAsString()
        );

        List<String> aliases = new ArrayList<>();
        entityObj.get("aliases").getAsJsonArray().forEach(JSONAliasElem -> {
            aliases.add(JSONAliasElem.getAsString());
        });
        fileHistory.setAliases(aliases);

        Set<String> tags = new HashSet<>();
        entityObj.get("fileTypes").getAsJsonArray().forEach(JSONTagElem -> {
            tags.add(JSONTagElem.getAsString());
        });
        fileHistory.setFileTypes(tags);

        EntityDeserializer.deserializeCodeEntityFields(fileHistory, entityObj);

        fileHistoryMap.put(fileHistoryId, fileHistory);
        return fileHistory;
    }

}
