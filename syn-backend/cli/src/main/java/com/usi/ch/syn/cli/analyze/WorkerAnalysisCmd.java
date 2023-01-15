package com.usi.ch.syn.cli.analyze;

import com.usi.ch.syn.analyzer.ProjectAnalyzer;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.storage.StorageException;
import com.usi.ch.syn.core.storage.StorageHelper;
import picocli.CommandLine;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;


// syn analyze worker -p 1 -w <worker.json> -g <git_repo> -o result.json
@CommandLine.Command(name = "worker", description = "perform a partial analysis specified in the worker file")
public class WorkerAnalysisCmd implements Callable<Integer> {

    @CommandLine.Option(names = {"-p", "--project"}, description = "the id of the project that must be analyzed")
    int projectId;

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = {"-g", "--git"}, description = "path of the project repository")
    private File gitRepo = null;

    @CommandLine.Option(names = {"-w", "--analysisWorker"}, description = "path of the analysis worker descriptor")
    private File analysisWorkerDescriptor = null;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file")
    private File outputFile = null;


    @Override
    public Integer call() {
        Optional<Project> optionalProject = ProjectFactory.getInstance().getProject(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            AnalysisWorkDescriptor analysisWorkDescriptor;
            try {
                analysisWorkDescriptor = StorageHelper.loadAnalysisWorkDescriptor(analysisWorkerDescriptor.toPath());
            } catch (StorageException exception) {
                System.out.println(exception.getLocalizedMessage());
                return 1;
            }

            if (gitRepo != null) {
                project.setPath(gitRepo.getAbsolutePath());
            }

            if (outputFile == null) {
                outputFile = new File(project.getName() + "_analysis_" + System.currentTimeMillis() + ".json");
            }

            ProjectAnalysisResult projectAnalysisResult = new ProjectAnalyzer().runAnalysis(analysisWorkDescriptor);

            //TODO Decide if this function should reside in ProjectFactory
            ProjectFactory.getInstance().storeProjectAnalysis(projectAnalysisResult, outputFile);
            return 0;

        } else {
            System.out.println("Unable to locate project with id " + projectId);
            return 1;
        }
    }

}