package com.usi.ch.syn.cli.analyze;

import com.usi.ch.syn.core.model.analysis.AnalysisStatus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AnalysisThreadWatcher {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private BlockingQueue<AnalysisStatus> statusAnalysisQueue;
    private final Map<Thread, AnalysisStatus> lastThreadReport = new HashMap<>();
    private ScheduledFuture<?> watcherHandle;
    private List<Thread> threadList;
    private DecimalFormat df = new DecimalFormat("#.##");
    NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    AtomicInteger pastTotalProcessedCommits = new AtomicInteger(0);
    private final Integer LOGGING_INTERVAL_SECONDS = 60 * 2;


    public AnalysisThreadWatcher(BlockingQueue<AnalysisStatus> statusAnalysisQueue, List<Thread> threadList) {
        this.statusAnalysisQueue = statusAnalysisQueue;
        this.threadList = threadList;
    }

    public void start() {
        System.out.println("Starting thread watcher");
         watcherHandle = scheduler.scheduleAtFixedRate(() -> {
             List<AnalysisStatus> elements = new ArrayList<>();
             statusAnalysisQueue.drainTo(elements);
             elements.forEach(analysisStatus -> lastThreadReport.put(analysisStatus.thread(), analysisStatus));

             AtomicInteger totalProcessedCommits = new AtomicInteger();
             threadList.forEach(thread -> {
                 AnalysisStatus analysisStatus =  lastThreadReport.get(thread);
                 if (analysisStatus == null) {
                     System.out.println("[UNKNOWN]" + thread.getName());
                 } else {
                     double threadProgress = (analysisStatus.commitsAnalyzed() / (double) (analysisStatus.commitsAnalyzed() + analysisStatus.remainingCommits())) * 100;

                     System.out.println("[" + analysisStatus.status() + "] " + thread.getName() + " : "
                             + "progress: " + df.format(threadProgress) + "% "
                             + "RC: " + nf.format(analysisStatus.remainingCommits()) + " "
                             + "PV: " + nf.format(analysisStatus.commitsAnalyzed()) + " "
                             + "FH: " + nf.format(analysisStatus.discoveredFileHistories()) + " "
                             + "FV: " + nf.format(analysisStatus.discoveredFileVersions())
                     );
                     totalProcessedCommits.addAndGet(analysisStatus.commitsAnalyzed());
                 }
             });

             if (totalProcessedCommits.get() != 0 && pastTotalProcessedCommits.get() != 0) {
                 System.out.println("Commit speed rate: " + (totalProcessedCommits.get() - pastTotalProcessedCommits.get()) / LOGGING_INTERVAL_SECONDS + " commits/s");
             }
             pastTotalProcessedCommits.set(totalProcessedCommits.get());

             System.out.println("");
         }, 0, LOGGING_INTERVAL_SECONDS, SECONDS);
    }

    public void stop() {
        watcherHandle.cancel(true);
    }
}
