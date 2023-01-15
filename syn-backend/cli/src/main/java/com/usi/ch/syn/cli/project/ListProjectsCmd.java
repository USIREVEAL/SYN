package com.usi.ch.syn.cli.project;

import com.usi.ch.syn.core.storage.ProjectFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "list", description = "list projects")
public class ListProjectsCmd implements Callable<Integer> {

    @Override
    public Integer call() {

        System.out.printf("List of available projects %n");
        ProjectFactory.getInstance().getListAvailableProjects().forEach(projectFileIdentifier -> {
            System.out.printf("\t -%d: %s %n", projectFileIdentifier.id(), projectFileIdentifier.name());
        });
        return 0;
    }
}
