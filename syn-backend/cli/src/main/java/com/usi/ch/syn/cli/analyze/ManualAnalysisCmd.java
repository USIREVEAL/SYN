package com.usi.ch.syn.cli.analyze;

import com.usi.ch.syn.analyzer.JGitAnalysisWorkerDescriptorFactory;
import com.usi.ch.syn.analyzer.ProjectAnalyzer;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

// syn analyze manual -p 1 -g <git_repo> -rC <recent_commit> -lC <last commit> -o result.json
@CommandLine.Command(name = "manual", description = "perform a manual partial analysis")
public class ManualAnalysisCmd implements Callable<Integer> {

    @CommandLine.Option(names = {"-p", "--project"}, description = "the id of the project that must be analyzed")
    int projectId;

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = {"-g", "--git"}, description = "path of the project repository")
    private File gitRepo = null;

    @CommandLine.Option(names = {"-rc", "--recentCommit"}, description = "hash of the first commit being analyzed")
    private String mostRecentCommit = "";

    @CommandLine.Option(names = {"-lc", "--lastCommit"}, description = "hash of the last commit being analyzed")
    private String leastRecentCommit = "";

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file")
    private File outputFile = null;




    @Override
    public Integer call() {
        Optional<Project> optionalProject = ProjectFactory.getInstance().getProject(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();


            if (gitRepo != null) {
                project.setPath(gitRepo.getAbsolutePath());
            }

            if (outputFile == null) {
                outputFile = new File(project.getName() + "_analysis_" + System.currentTimeMillis() + ".json");
            }


            AnalysisWorkDescriptor analysisWorkDescriptor = new JGitAnalysisWorkerDescriptorFactory().createWithCommitBoundaries(project, mostRecentCommit, leastRecentCommit);
            ProjectAnalysisResult analysisResults = new ProjectAnalyzer().runAnalysis(analysisWorkDescriptor);

            //TODO Decide if this function should reside in ProjectFactory
            ProjectFactory.getInstance().storeProjectAnalysis(analysisResults, outputFile);

            return 0;

        } else {
            System.out.println("Unable to locate project with id " + projectId);
            return 1;
        }
    }
}