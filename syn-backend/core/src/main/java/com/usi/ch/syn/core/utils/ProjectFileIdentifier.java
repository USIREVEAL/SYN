package com.usi.ch.syn.core.utils;

import com.usi.ch.syn.core.model.project.Project;

public record ProjectFileIdentifier(long id, String name) {

    public static ProjectFileIdentifier fromProject(Project project) {
        return new ProjectFileIdentifier(project.getId(), project.getName());
    }

    public static ProjectFileIdentifier fromString(String str) {
        int strSeparatorIndex = str.indexOf('_');
        String projectIdStr = str.substring(0, strSeparatorIndex);
        String projectName = str.substring(strSeparatorIndex + 1);
        return new ProjectFileIdentifier(Integer.parseInt(projectIdStr), projectName);

    }

    public String toString() {
        return id + "_" + name;
    }
}