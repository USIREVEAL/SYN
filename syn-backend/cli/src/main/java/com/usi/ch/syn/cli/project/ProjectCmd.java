package com.usi.ch.syn.cli.project;

import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.model.project.Project;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "project", description = "Creates a new project", subcommands = {
        CreateProjectCmd.class, InspectProjectCmd.class, ListProjectsCmd.class
})
public class ProjectCmd implements Callable<Integer> {

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help = true;

    @Override
    public Integer call() {
        return 0;
    }
}
