package com.usi.ch.syn.core.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.project.LocalProject;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.storage.deserializer.*;
import com.usi.ch.syn.core.storage.serializer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.usi.ch.syn.core.utils.Config.SYN_DATA_PATH;
import static com.usi.ch.syn.core.utils.Config.getProjectAnalysisPath;

public class StorageHelper {

    private static final Logger logger = LoggerFactory.getLogger(StorageHelper.class);

    private StorageHelper() {
    }

    static void storeProject(ProjectAnalysisResult analysisResult) {
        storeProject(analysisResult, getProjectAnalysisPath(analysisResult).toFile());
    }

    public static void storeProject(ProjectAnalysisResult analysisResult, File outputFile) {
        try {
            Path filePath = Path.of(outputFile.getPath());
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);

            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.registerTypeAdapter(FileHistory.class, new FileHistorySerializer());
            gsonBuilder.registerTypeAdapter(FileVersion.class, new FileVersionSerializer());
            gsonBuilder.registerTypeAdapter(Project.class, new ProjectSerializer());
            gsonBuilder.registerTypeAdapter(RemoteProject.class, new ProjectSerializer());
            gsonBuilder.registerTypeAdapter(LocalProject.class, new ProjectSerializer());
            gsonBuilder.registerTypeAdapter(Change.class, new ChangeSerializer());
            gsonBuilder.registerTypeAdapter(ProjectVersion.class, new ProjectVersionSerializer());
            gsonBuilder.registerTypeAdapter(ProjectAnalysisResult.class, new ProjectAnalysisResultSerializer());
            gsonBuilder.setPrettyPrinting();

            logger.info("Storing analysis results in {}", outputFile.getAbsoluteFile());
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {

                Gson gson = gsonBuilder.create();
                JsonWriter jsonWriter = new JsonWriter(bufferedWriter);
                jsonWriter.beginObject()
                        .name("project").jsonValue(gson.toJson(analysisResult.getProject(), Project.class))
                        .name("analysisCompleted").jsonValue(gson.toJson(analysisResult.isAnalysisCompleted(), Boolean.class))
                        .name("firstCommit").jsonValue(gson.toJson(analysisResult.getFirstCommit(), String.class))
                        .name("lastCommit").jsonValue(gson.toJson(analysisResult.getLastCommit(), String.class))
                        .name("timestamp").jsonValue(gson.toJson(analysisResult.getTimestamp(), Long.class));

                jsonWriter.name("projectVersions").beginArray();
                for (ProjectVersion projectVersion : analysisResult.getProjectVersions()) {
                    gson.toJson(projectVersion, ProjectVersion.class, jsonWriter);
                    jsonWriter.flush();
                }
                jsonWriter.endArray();

                jsonWriter.name("fileHistories").beginArray();
                for (FileHistory fileHistory : analysisResult.getFileHistories()) {
                    gson.toJson(fileHistory, FileHistory.class, jsonWriter);
                    jsonWriter.flush();
                }
                jsonWriter.endArray();

                jsonWriter.name("fileVersions").beginArray();
                for (FileVersion fileVersion : analysisResult.getFileVersions()) {
                    gson.toJson(fileVersion, FileVersion.class, jsonWriter);
                    jsonWriter.flush();
                }

                jsonWriter.endArray();
                jsonWriter.endObject();


            } catch (IOException ex) {
                logger.error("Unable to write to file {}", outputFile);
            }
        } catch (IOException e) {
            logger.error("Unable to create file {}", outputFile.getAbsoluteFile());
        }
    }

    static Stream<Path> getAllStoredProjectsPaths() throws IOException {
        return Files.list(Path.of(SYN_DATA_PATH));
    }

    static Map<Integer, Path> getProjectAnalysisPathMap() {
        Map<Integer, Path> idProjectMap = new HashMap<>();
        try {
            Files.list(Path.of(SYN_DATA_PATH)).forEach(path -> {
                String fileName = path.getFileName().toString();
                String[] nameParts;
                if ((nameParts = fileName.split("_")).length > 0) {
                    try {
                        int projectID = Integer.parseInt(nameParts[0]);
                        idProjectMap.put(projectID, path);
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (IOException ex) {
            logger.error("Unable to list all files inside {}", SYN_DATA_PATH);
        }

        return idProjectMap;

    }

    public static ProjectAnalysisResult loadProjectAnalysis(Path projectAnalysisPath) {
        GsonBuilder gson = new GsonBuilder();

        FileHistoryDeserializer fileHistoryDeserializer = new FileHistoryDeserializer();
        FileVersionDeserializer fileVersionDeserializer = new FileVersionDeserializer(fileHistoryDeserializer.getFileHistoryMap());
        ProjectVersionDeserializer projectVersionDeserializer = new ProjectVersionDeserializer(fileVersionDeserializer.getFileVersionMap());
        ProjectAnalysisResultDeserializer projectAnalysisResultDeserializer = new ProjectAnalysisResultDeserializer();

        gson.registerTypeAdapter(FileHistory.class, fileHistoryDeserializer);
        gson.registerTypeAdapter(Project.class, new ProjectDeserializer());
        gson.registerTypeAdapter(RemoteProject.class, new ProjectDeserializer());
        gson.registerTypeAdapter(LocalProject.class, new ProjectDeserializer());
        gson.registerTypeAdapter(FileVersion.class, fileVersionDeserializer);
        gson.registerTypeAdapter(ProjectVersion.class, projectVersionDeserializer);
        gson.registerTypeAdapter(ProjectAnalysisResult.class, projectAnalysisResultDeserializer);

        try {
            String json = String.join("", Files.readAllLines(projectAnalysisPath));
            ProjectAnalysisResult projectAnalysisResult = gson.create().fromJson(json, ProjectAnalysisResult.class);

            if (projectAnalysisResult.isAnalysisCompleted()) {
                Project project = projectAnalysisResult.getProject();
                ProjectHistory projectHistory = new ProjectHistory(project, projectAnalysisResult.getFileHistories(), projectAnalysisResult.getProjectVersions());
                project.setProjectHistory(projectHistory);
            }

            return projectAnalysisResult;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Unable to read analysis from file located at{}", projectAnalysisPath.toAbsolutePath());
        }


        return null;
    }

    public static void storeAnalysisWorkDescriptor(AnalysisWorkDescriptor analysisWorkDescriptor, File outputFile) {
        try {
            Path filePath = Path.of(outputFile.getPath());
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);

            GsonBuilder gson = new GsonBuilder();

            gson.registerTypeAdapter(Project.class, new ProjectSerializer());
            gson.registerTypeAdapter(RemoteProject.class, new ProjectSerializer());
            gson.registerTypeAdapter(LocalProject.class, new ProjectSerializer());
            gson.setPrettyPrinting();

            logger.info("Storing analysis results in {}", outputFile.getAbsoluteFile());
            try (FileWriter writer = new FileWriter(outputFile);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write(gson.create().toJson(analysisWorkDescriptor));
            } catch (IOException ex) {
                logger.error("Unable to write to file {}", outputFile);
            }
        } catch (IOException e) {
            logger.error("Unable to create file {}", outputFile.getAbsoluteFile());
        }
    }

    public static AnalysisWorkDescriptor loadAnalysisWorkDescriptor(Path analysisWorkerPath) throws StorageException {
        GsonBuilder gson = new GsonBuilder();

        gson.registerTypeAdapter(RemoteProject.class, new ProjectDeserializer());
        gson.registerTypeAdapter(LocalProject.class, new ProjectDeserializer());
        gson.registerTypeAdapter(Project.class, new ProjectDeserializer());
        gson.registerTypeAdapter(AnalysisWorkDescriptor.class, new AnalysisWorkerDescriptorDeserializer());
        try {

            String json = String.join("", Files.readAllLines(analysisWorkerPath));
            return gson.create().fromJson(json, AnalysisWorkDescriptor.class);

        } catch (IOException e) {
            throw new StorageException("Unable to open file at " + analysisWorkerPath);
        } catch (JsonSyntaxException e) {
            throw new StorageException("Unable to deserialize an AnalysisWorkerDescriptor from file at " + analysisWorkerPath + "");
        }
    }

    static void deleteProjectAnalysis(Path projectAnalysisPath) {

        File projectAnalysisFile = projectAnalysisPath.toFile();
        if (projectAnalysisFile.delete()) {
            logger.info("Deleted project in {}", projectAnalysisFile);
        }
    }


}
