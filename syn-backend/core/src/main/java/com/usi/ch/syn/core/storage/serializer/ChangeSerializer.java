package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.change.*;

import java.lang.reflect.Type;

public class ChangeSerializer implements JsonSerializer<Change> {


    @Override
    public JsonElement serialize(Change change, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject changeObj;

        if (change.isModify()) {
            changeObj = context.serialize(change, FileModification.class).getAsJsonObject();

        } else if (change.isAdd()) {
            changeObj = context.serialize(change, FileAddition.class).getAsJsonObject();
        } else if (change.isDelete()) {
            changeObj = context.serialize(change, FileDeletion.class).getAsJsonObject();
        } else if (change.isRename()) {
            changeObj = context.serialize(change, FileRenaming.class).getAsJsonObject();
        } else if (change.isMove()) {
            changeObj = context.serialize(change, FileMoving.class).getAsJsonObject();
        } else {
            changeObj = context.serialize(change, Change.class).getAsJsonObject();
        }
        changeObj.addProperty("type", change.getClass().getSimpleName());


        return changeObj;
    }



}
