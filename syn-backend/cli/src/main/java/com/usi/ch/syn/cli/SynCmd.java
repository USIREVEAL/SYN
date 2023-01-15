package com.usi.ch.syn.cli;

import com.usi.ch.syn.cli.analyze.AnalyzeCmd;
import com.usi.ch.syn.cli.project.InspectProjectCmd;
import com.usi.ch.syn.cli.project.ListProjectsCmd;
import com.usi.ch.syn.cli.project.ProjectCmd;
import com.usi.ch.syn.cli.util.UtilCmd;
import picocli.CommandLine;

@CommandLine.Command(name = "syn", subcommands = {
    UtilCmd.class, ProjectCmd.class, AnalyzeCmd.class
})
public class SynCmd implements Runnable {

    @Override
    public void run() {
        System.out.println("This is the CLI of SYN");
    }

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    public static void main(String... args) {
        CommandLine commandLine = new CommandLine(new SynCmd());

        int exitCode;
        try {
            commandLine.parseArgs(args);
            exitCode = commandLine.execute(args);
        }catch (CommandLine.PicocliException e) {
            commandLine.usage(System.out);
            exitCode = -1;
        }
        System.exit(exitCode);
    }
}
