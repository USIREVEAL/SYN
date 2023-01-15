package com.usi.ch.syn.core.model.project;

import lombok.Getter;

import static com.usi.ch.syn.core.utils.Config.getGitProjectFolderPath;

@Getter
public class RemoteProject extends Project {

    private final String projectURL;

    protected RemoteProject(int id, String name, String location, String projectURL) {
        super(id, name, location);
        this.projectURL = projectURL;
    }

    public static Project create(int id, String name, String url) {
        String projectPath = getGitProjectFolderPath(name, id).toString();
        return new RemoteProject(id, name, projectPath, url);
    }

    public static Project create(int id, String name, String projectPath, String url) {
        return new RemoteProject(id, name, projectPath, url);
    }


}
