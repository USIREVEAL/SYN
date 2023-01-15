package com.usi.ch.syn.core.utils;

import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class Config {
    private Config() { }
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    public static final String SYN_PATH = System.getenv("SYN_HOME") != null ? System.getenv("SYN_HOME")  : System.getProperty("user.home") + "/.syn";
    public static final String SYN_DATA_PATH = SYN_PATH + "/data";
    public static final String SYN_GIT_PROJECTS_PATH = SYN_PATH + "/projects";

    static {
        File synDir = new File(SYN_PATH);
        if (synDir.exists() || synDir.mkdir()) {
            File synData = new File(SYN_DATA_PATH);
            File synProjects = new File(SYN_GIT_PROJECTS_PATH);
            if (synData.exists() || synData.mkdir() && synProjects.exists() || synProjects.mkdir())  {
                logger.info("Using working directory: {}", synDir.getAbsoluteFile());
            }
        }
    }


    // SYN_GIT_PROJECTS_PATH/<projectID>_<name>
    public static Path getGitProjectFolderPath(String projectName, int projectID) {
        return Path.of(SYN_GIT_PROJECTS_PATH + "/" + projectID + "_" + projectName);
    }

    // SYN_DATA_PATH/<projectID>_<name>.json
    public static Path getProjectAnalysisPath(ProjectAnalysisResult analysisResult) {
        return Path.of(SYN_DATA_PATH + "/" + getProjectAnalysisFileName(analysisResult));
    }

    //<projectID>_<name>.json
    private static String getProjectAnalysisFileName(ProjectAnalysisResult analysisResult) {
        return ProjectFileIdentifier.fromProject(analysisResult.getProject()) + ".json";
    }

    public static Path getSynAnalysisMapFile() {
        return Path.of(SYN_DATA_PATH + "/.syn_index.json");
    }


}
