package com.usi.ch.syn.cli.project;

import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "create", description = "Creates a new project")
public class CreateProjectCmd implements Callable<Integer> {

    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = { "-n", "--name" }, required = true, description = "The name of the project")
    private String name;

    @CommandLine.Option(names = { "-p", "--path" }, description = "The path of the repository (can also be a url)")
    private String path;

    @Override
    public Integer call() {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        Project project = ProjectFactory.getInstance().createProject(name, path);
        System.out.printf("created new project with id: %d %n", project.getId());

        return 0;
    }


}
