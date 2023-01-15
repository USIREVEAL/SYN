package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.usi.ch.syn.core.model.CodeEntity;
import com.usi.ch.syn.core.model.Entity;

public class EntitySerializer {

    public static void serializeCodeEntityFields(CodeEntity entity, JsonObject object) {
        serializeEntityFields(entity, object);
        JsonArray entityMetricsArray = new JsonArray();
        entity.getMetrics().forEach(p -> {
            JsonObject propertyJSON = new JsonObject();
            propertyJSON.addProperty("name", p.name());
            propertyJSON.addProperty("value", p.value());

            entityMetricsArray.add(propertyJSON);
        });

        object.add("metrics", entityMetricsArray);
    }

    public static void serializeEntityFields(Entity entity, JsonObject object) {
        object.addProperty("id", entity.getId());
    }


}
