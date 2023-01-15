package com.usi.ch.syn.core.storage.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.usi.ch.syn.core.model.CodeEntity;
import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.Metric;

public class EntityDeserializer {

    public static void deserializeCodeEntityFields(CodeEntity entity, JsonObject object) {
        object.getAsJsonArray("metrics").forEach(metric -> {
            JsonObject metricJsonObj = metric.getAsJsonObject();

            entity.getMetrics().add(new Metric(
                    metricJsonObj.get("name").getAsString(),
                    metricJsonObj.get("value").getAsString()
            ));
        });
    }

}
