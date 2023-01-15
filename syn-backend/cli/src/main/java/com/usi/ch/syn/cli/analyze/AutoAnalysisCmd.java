package com.usi.ch.syn.cli.analyze;

import com.usi.ch.syn.analyzer.JGitAnalysisWorkerDescriptorFactory;
import com.usi.ch.syn.analyzer.ProjectAnalyzer;
import com.usi.ch.syn.analyzer.ProjectAnalyzerJoiner;
import com.usi.ch.syn.core.model.analysis.AnalysisStatus;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.analysis.ProjectAnalysisResult;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.storage.StorageHelper;
import com.usi.ch.syn.core.utils.Config;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static com.usi.ch.syn.core.utils.Config.SYN_DATA_PATH;
import static java.util.concurrent.TimeUnit.SECONDS;


// syn analyze auto -p 1 -o result.json
@CommandLine.Command(name = "auto", description = "perform an automatic analysis")
public class AutoAnalysisCmd implements Callable<Integer> {

    @CommandLine.Option(names = {"-p", "--project"}, description = "the id of the project that must be analyzed")
    int projectId;

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @CommandLine.Option(names = {"-o", "--output"}, description = "output file")
    private File outputFile = null;

    @CommandLine.Option(names = {"-t", "--threadCount"}, description = "Number of thread that will be instantiated")
    private Integer threadCount = 5;

    private final String ANALYSIS_THREAD_PREFIX = "Thread_Analysis_";


    @Override
    public Integer call() throws IOException, InterruptedException {
        Optional<Project> optionalProject = ProjectFactory.getInstance().getProject(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            if (project instanceof RemoteProject remoteProject) {

                System.out.println("Starting automatic project analysis");
                System.out.println("Number of threads: " + threadCount);

                Path tmpDirPath = Path.of(System.getProperty("java.io.tmpdir") + File.separator + "syn_analysis_repos");
                if (Files.notExists(tmpDirPath)) {
                    Files.createDirectory(tmpDirPath);
                }

                List<Project> mockProjectList = new ArrayList<>();
                for (int i = 0; i < threadCount; i++) {
                    File gitRepoDir = new File(tmpDirPath + File.separator + project.getName() + "_" + i);
                    Files.deleteIfExists(gitRepoDir.toPath());
                    Project mockProject = RemoteProject.create(i, project.getName(), gitRepoDir.getAbsolutePath(), remoteProject.getProjectURL());
                    mockProjectList.add(mockProject);
                }
                System.out.println("Working location: " + tmpDirPath);

                System.out.println("Preparing the workers for the analysis");
                List<AnalysisWorkDescriptor> analysisWorkDescriptorList = new JGitAnalysisWorkerDescriptorFactory().createOnProjectHistoryChunks(project, threadCount);
                printWorkerDescription(analysisWorkDescriptorList);
                assert analysisWorkDescriptorList.size() == threadCount;

                System.out.println("Starting the analysis");
                ConcurrentLinkedQueue<ProjectAnalysisResult> analysisResultsQueue = new ConcurrentLinkedQueue<>();
                BlockingQueue<AnalysisStatus> statusAnalysisQueue = new LinkedBlockingQueue<>();
                List<Thread> analysisThreadList = new ArrayList<>();
                for (int i = 0; i < threadCount; i++) {
                    Project mockProject = mockProjectList.get(i);
                    AnalysisWorkDescriptor analysisWorkDescriptor = analysisWorkDescriptorList.get(i);
                    AnalysisWorkDescriptor mockAnalysisWorkDescriptor = new AnalysisWorkDescriptor(mockProject, analysisWorkDescriptor.commits());
                    Thread thread = new Thread(() -> {
                        ProjectAnalysisResult projectAnalysisResult = new ProjectAnalyzer().runAnalysis(mockAnalysisWorkDescriptor, statusAnalysisQueue);
                        analysisResultsQueue.add(projectAnalysisResult);
                    });
                    thread.setName(ANALYSIS_THREAD_PREFIX + i);
                    analysisThreadList.add(thread);
                }

                AnalysisThreadWatcher analysisThreadWatcher = new AnalysisThreadWatcher(statusAnalysisQueue, analysisThreadList);
                analysisThreadWatcher.start();

                analysisThreadList.forEach(Thread::start);
                System.out.println("Analysis started");
                analysisThreadList.forEach(thread -> {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                analysisThreadWatcher.stop();
                System.out.println("Analysis finished");
                ProjectAnalysisResult[] projectAnalysisResults = new ProjectAnalysisResult[threadCount];
                analysisResultsQueue.forEach(mockedProjectAnalysisResult -> {
                    ProjectAnalysisResult projectAnalysisResult = new ProjectAnalysisResult(
                            project,
                            mockedProjectAnalysisResult.isAnalysisCompleted(),
                            mockedProjectAnalysisResult.getTimestamp(),
                            mockedProjectAnalysisResult.getFirstCommit(),
                            mockedProjectAnalysisResult.getLastCommit(),
                            mockedProjectAnalysisResult.getProjectVersions(),
                            mockedProjectAnalysisResult.getFileHistories(),
                            mockedProjectAnalysisResult.getFileVersions()
                    );
                    projectAnalysisResults[mockedProjectAnalysisResult.getProject().getId()] = projectAnalysisResult;
                });
                System.out.println("Saving a backup copy of the results");

                Path dirBackupPath =  Path.of(SYN_DATA_PATH + File.separator + "backup_analysis_" + project.getName());
                if (Files.notExists(dirBackupPath))
                    Files.createDirectory(dirBackupPath);

                for (int i = 0; i < projectAnalysisResults.length; i++) {
                    ProjectAnalysisResult projectAnalysisResult = projectAnalysisResults[i];
                    Path tmpFilePath = Path.of(dirBackupPath + File.separator + i + ".json");
                    System.out.println("Writing: " + tmpFilePath);
                    StorageHelper.storeProject(projectAnalysisResult, tmpFilePath.toFile());
                }

                System.out.println("Joining analysis results together");
                ProjectAnalysisResult analysisResult = new ProjectAnalyzerJoiner().joinAnalysisResults(projectAnalysisResults);
                System.out.println("Done");

                if (outputFile == null) {
                    ProjectFactory.getInstance().storeProjectAnalysis(analysisResult);
                } else {
                    ProjectFactory.getInstance().storeProjectAnalysis(analysisResult, outputFile);
                }

                return 0;
            } else {
                System.out.println("Only remote project can be analyzed with auto analysis");
                return 1;
            }
        } else {
            System.out.println("Unable to locate project with id " + projectId);
            return 1;
        }
    }

    private void printWorkerDescription(List<AnalysisWorkDescriptor> analysisWorkDescriptorList) {
        System.out.println("Workers detail");
        for (int i = 0; i < threadCount; i++) {
            System.out.println(ANALYSIS_THREAD_PREFIX + i + " : " + analysisWorkDescriptorList.get(i).commits().size() + " commits");
        }
        System.out.println("");
    }
}