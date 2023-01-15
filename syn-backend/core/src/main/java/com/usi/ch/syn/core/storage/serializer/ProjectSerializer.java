package com.usi.ch.syn.core.storage.serializer;

import com.google.gson.*;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class ProjectSerializer implements JsonSerializer<Project> {
    private static final Logger logger = LoggerFactory.getLogger(FileHistorySerializer.class);

//    public static void serializeProjectDescriptor(Project project) {
//        Path projectDescriptorPath = getProjectDescriptorPath(project);
//
//        GsonBuilder gson = new GsonBuilder();
//
//        ProjectSerializer projectSerializer = new ProjectSerializer();
//        gson.registerTypeAdapter(LocalProject.class, projectSerializer);
//        gson.registerTypeAdapter(RemoteProject.class, projectSerializer);
//        gson.setPrettyPrinting();
//
//        try (FileWriter writer = new FileWriter(projectDescriptorPath.toFile());
//             BufferedWriter bw = new BufferedWriter(writer)) {
//            bw.write(gson.create().toJson(project));
//        } catch(IOException ex){
//            logger.error("Unable to write to file {}", projectDescriptorPath);
//        }
//    }


    @Override
    public JsonElement serialize(Project project, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();


        EntitySerializer.serializeCodeEntityFields(project, object);
        object.addProperty("name", project.getName());
        object.addProperty("path", project.getPath());
//        object.addProperty("lastAnalysisTs", project.getLastAnalysisTs());

        if (project instanceof RemoteProject remoteProject) {
            object.addProperty("projectURL", remoteProject.getProjectURL());
        }

        return object;
    }

}
