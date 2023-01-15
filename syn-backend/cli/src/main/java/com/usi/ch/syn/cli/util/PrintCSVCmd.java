package com.usi.ch.syn.cli.util;

import com.usi.ch.syn.analyzer.ProjectAnalysisUtils;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "csv", description = "analyze a project")
public class PrintCSVCmd implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", paramLabel = "<id>", description = "the id of the project that must be analyzed")
    int projectID;

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file")
    private File outputFile = null;

    @Override
    public Integer call() {
        Optional<Project> optProject = ProjectFactory.getInstance().getProject(projectID);
        if (optProject.isPresent()) {
            Project project = optProject.get();

            if (outputFile == null) {
                outputFile = new File(project.getName() + "_commits.csv");
            }

            ProjectAnalysisUtils.printCommitCSV(project, outputFile);

            return 0;
        } else {
            System.out.printf("Unable to find a project with id %d %n", projectID);
            System.out.println("Run syn list to have a list of available projects %n");
            return 1;
        }
    }
}