package com.usi.ch.syn.core.model.project;

public class LocalProject extends Project {

    protected LocalProject(int id, String name, String location) {
        super(id, name, location);
    }

    public static Project create(int id, String name, String path) {
        return new LocalProject(id, name, path);
    }

}
