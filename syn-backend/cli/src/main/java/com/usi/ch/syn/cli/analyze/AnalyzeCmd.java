package com.usi.ch.syn.cli.analyze;

import picocli.CommandLine;

import java.util.concurrent.Callable;


@CommandLine.Command(name = "analyze", description = "analyze a project", subcommands = {
        AutoAnalysisCmd.class, JoinAnalysisCmd.class, ManualAnalysisCmd.class,  PrepareAnalysisCmd.class, WorkerAnalysisCmd.class
})
public class AnalyzeCmd implements Callable<Integer> {
    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help = true;

    @Override
    public Integer call() {
        return 0;
    }
}