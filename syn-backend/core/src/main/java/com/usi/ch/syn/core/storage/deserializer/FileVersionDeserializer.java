package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;



public class FileVersionDeserializer implements JsonDeserializer<FileVersion> {

    private static final Logger logger = LoggerFactory.getLogger(FileVersionDeserializer.class);
    private final Map<Integer, FileHistory> fileHistoryMap;
    @Getter private final Map<Integer, FileVersion> fileVersionMap = new HashMap<>();


    public FileVersionDeserializer(Map<Integer, FileHistory> fileHistoryMap) { this.fileHistoryMap = fileHistoryMap; }

//    public void deserializeFileVersions(Project project) {
//        GsonBuilder gson = new GsonBuilder();
//
//        gson.registerTypeAdapter(FileVersion.class, this);
//        gson.registerTypeAdapter(Change.class, new ChangeDeserializer());
//
//        Path entityPath = getFileVersionPath(project);
//
//        try {
//            String json = String.join("", Files.readAllLines(entityPath));
//            gson.create().fromJson(json, TypeToken.getParameterized(List.class, FileVersion.class).getType());
//        } catch (IOException e) {
//            logger.error("Error ",  e);
//        }
//    }

    @Override
    public FileVersion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject versionJSONObj = json.getAsJsonObject();
        int id = versionJSONObj.get("id").getAsInt();
        int fileHistoryId = versionJSONObj.get("fileHistory").getAsInt();

        Change change = ChangeDeserializer.deserializeChange(versionJSONObj.get("change").toString());


        FileVersion fileVersion = new FileVersion(id, change, fileHistoryMap.get(fileHistoryId));
        fileVersionMap.put(id, fileVersion);

        if (versionJSONObj.has("previous")) {
            FileVersion previousVersion = fileVersionMap.get(versionJSONObj.get("previous").getAsInt());
            previousVersion.setNext(fileVersion);
            fileVersion.setPrevious(previousVersion);
        }

        EntityDeserializer.deserializeCodeEntityFields(fileVersion, versionJSONObj);
        return fileVersion;
    }
}
