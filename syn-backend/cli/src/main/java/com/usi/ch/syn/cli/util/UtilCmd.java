package com.usi.ch.syn.cli.util;

import picocli.CommandLine;

import java.util.concurrent.Callable;


@CommandLine.Command(name = "util", subcommands = {
        PrintCSVCmd.class
})
public class UtilCmd implements Callable<Integer> {


    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help and exit")
    boolean help;

    @Override
    public Integer call() {
        return 0;
    }
}