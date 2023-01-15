package com.usi.ch.syn.core.model;

import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.project.LocalProject;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.core.model.version.FileVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;



public class EntityIdTranslator {


    private static final byte MAX_CLASS_IDENTIFIER = 127;
//    private static final byte MIN_CLASS_IDENTIFIER = 0;

    public final static int FIRST_AVAIL_ID = 100000000;


    /**
     * This map is needed to properly generate ids based on the entity's class
     */
    private static final Map<Class<? extends Entity>, Byte> entityIdGenerationMap = new HashMap<>();

    public static int generateEntityId(Class<? extends Entity> clazz, int id) {

        if (!entityIdGenerationMap.containsKey(clazz)) {
            try {
                IdClassIdentifier classIdentifier = clazz.getAnnotation(IdClassIdentifier.class);
                entityIdGenerationMap.put(clazz, classIdentifier.value());
            } catch (NullPointerException exception) {
                entityIdGenerationMap.put(clazz, MAX_CLASS_IDENTIFIER);
            }
        }

        short classMultiplier = entityIdGenerationMap.get(clazz);
        return classMultiplier * FIRST_AVAIL_ID + id;
    }

    public static int getEntityOrder(int entityId) {
        return entityId %  FIRST_AVAIL_ID;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface IdClassIdentifier {
        byte value();
    }

}


