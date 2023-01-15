package com.usi.ch.syn.cli.analyze;

import com.usi.ch.syn.analyzer.JGitAnalysisWorkerDescriptorFactory;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.storage.StorageHelper;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;


// syn analyze worker -p 1 -o <folder> -wn -g
@CommandLine.Command(name = "prepare", description = "perform a partial analysis specified in the worker file")
public class PrepareAnalysisCmd implements Callable<Integer> {

    @CommandLine.Option(names = {"-p", "--project"}, description = "the id of the project that must be analyzed")
    int projectId;

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = {"-g", "--git"}, description = "path of the project repository")
    private File gitRepo = null;

    @CommandLine.Option(names = {"-wn", "--analysisWorkersNumber"}, description = "The number of worker that must be prepared for this analysis")
    private Integer analysisWorkersNumber = 10;

    @CommandLine.Option(names = {"-of", "--outputFolder"}, description = "the output folder path where the workers will be stored")
    private File outputDirectory = null;


    @Override
    public Integer call() {
        Optional<Project> optionalProject = ProjectFactory.getInstance().getProject(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            if (gitRepo != null) {
                project.setPath(gitRepo.getAbsolutePath());
            }

            if (outputDirectory == null) {
                outputDirectory = new File(project.getName() + "_workers_" + System.currentTimeMillis());
                if (!outputDirectory.mkdir()) {
                    System.out.println("Unable to create a new directory in " + outputDirectory.getPath());
                    return 1;
                }
            }


            List<AnalysisWorkDescriptor> analysisWorkDescriptorList = new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, analysisWorkersNumber);
            for (int i = 0; i < analysisWorkDescriptorList.size(); i++) {
                AnalysisWorkDescriptor analysisWorkDescriptor = analysisWorkDescriptorList.get(i);
                File workerFile = new File(outputDirectory + File.separator + "worker_" + i + ".json");
                StorageHelper.storeAnalysisWorkDescriptor(analysisWorkDescriptor, workerFile);
            }
            return 0;
        } else {
            System.out.println("Unable to locate project with id " + projectId);
            return 1;
        }
    }

}