package com.usi.ch.syn.cli.analyze;

import com.usi.ch.syn.analyzer.ProjectAnalyzerJoiner;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.storage.StorageHelper;
import com.usi.ch.syn.core.utils.Config;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "join", description = "perform a partial analysis specified in the worker file")
public class JoinAnalysisCmd implements Callable<Integer> {

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Parameters(index = "0", paramLabel = "JSON", description = "The id of the project that must be analyzed", arity = "1..*")
    File[] projectAnalysisResultFiles;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file")
    private File outputFile = null;

    @Override
    public Integer call() {

        ProjectAnalysisResult[] projectAnalysisResults = new ProjectAnalysisResult[projectAnalysisResultFiles.length];
        for (int i = 0; i < projectAnalysisResultFiles.length; i++) {
            System.out.printf("Deserializing : %s %n", projectAnalysisResultFiles[i].getAbsoluteFile());
            ProjectAnalysisResult projectAnalysisResult = StorageHelper.loadProjectAnalysis(Path.of(projectAnalysisResultFiles[i].getPath()));
            projectAnalysisResults[i] = projectAnalysisResult;
        }


        assert projectAnalysisResults[0] != null;
        Project project = projectAnalysisResults[0].getProject();
//        for (ProjectAnalysisResult projectAnalysisResult : projectAnalysisResults) {
//            if (!projectAnalysisResult.getProject().equals(project)) {
//                System.out.println("All the analysis must be done on the same project");
//                return 1;
//            }
//        }

        System.out.println("Joining analysis results");
        ProjectAnalysisResult analysisResult = new ProjectAnalyzerJoiner().joinAnalysisResults(projectAnalysisResults);
        System.out.println("Joined");
        if (outputFile == null) {
            ProjectFactory.getInstance().storeProjectAnalysis(analysisResult);
        } else {
            ProjectFactory.getInstance().storeProjectAnalysis(analysisResult, outputFile);
        }

        return 0;
    }

}