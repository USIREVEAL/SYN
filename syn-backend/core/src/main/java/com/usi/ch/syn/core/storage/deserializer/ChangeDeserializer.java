package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.*;

import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.change.*;

import java.lang.reflect.Type;


public class ChangeDeserializer implements JsonDeserializer<Change> {

    public static Change deserializeChange(String change) {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Change.class, new ChangeDeserializer());
        return gson.create().fromJson(change, Change.class);
    }

    @Override
    public Change deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String changeClass = object.get("type").getAsString();

        if (changeClass.equals(FileAddition.class.getSimpleName())) {
            return  context.deserialize(json, FileAddition.class);
        } else if (changeClass.equals(FileDeletion.class.getSimpleName())) {
            return  context.deserialize(json, FileDeletion.class);
        } else if (changeClass.equals(FileModification.class.getSimpleName())) {
            return  context.deserialize(json, FileModification.class);
        } else if (changeClass.equals(FileRenaming.class.getSimpleName())) {
            return  context.deserialize(json, FileRenaming.class);
        } else if (changeClass.equals(FileMoving.class.getSimpleName())) {
            return  context.deserialize(json, FileMoving.class);
        }

        return  context.deserialize(json, Change.class);
    }
}
