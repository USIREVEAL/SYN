package com.usi.ch.syn.core.model.view;

import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.model.view.specification.ViewSpecification;
import com.usi.ch.syn.core.model.view.specification.ViewSpecificationProcessor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class View {
    private final ViewSpecificationProcessor viewSpecificationProcessor;
    @Getter
    private final List<MusicSheet> musicSheets = new ArrayList<>();
    @Getter
    private List<ViewAnimation> viewAnimationList = new ArrayList<>();

    public View(ViewSpecification viewSpecification, ProjectHistory projectHistory) {
        viewSpecificationProcessor = new ViewSpecificationProcessor(viewSpecification, projectHistory);
        computeAnimationFrames();
        composeMusic();
    }

    public View(ViewSpecification viewSpecification, ProjectHistory projectHistory, List<ViewAnimation> newViewAnimationList) {
        viewSpecificationProcessor = new ViewSpecificationProcessor(viewSpecification, projectHistory);
        viewAnimationList = newViewAnimationList;
    }

    private void computeAnimationFrames() {
        List<List<ProjectVersion>> groupedProjectVersions = viewSpecificationProcessor.getGroupedProjectVersions();
        Map<FileHistory, FileVersion> fileHistoryLastFileVersionCache = new HashMap<>();
        long baseTs = groupedProjectVersions.get(0).get(0).getTimestamp();
        long lastProjectVersionId = groupedProjectVersions.get(0).get(0).getId();
        for (int i = 1; i <= groupedProjectVersions.size(); i++) {
            List<ProjectVersion> frameProjectVersions = groupedProjectVersions.get(i - 1);

            if (frameProjectVersions.size() > 0) {
                lastProjectVersionId = frameProjectVersions.get(frameProjectVersions.size() - 1).getId();
            }

            List<FileVersion> frameFileVersions = getLastFHFileVersion(frameProjectVersions, fileHistoryLastFileVersionCache);

            ViewAnimation viewAnimation = new ViewAnimation(i);
            viewAnimation.setRepresentedEntities(frameProjectVersions);

            viewAnimation.setTsFrom(viewSpecificationProcessor.getViewAnimationTsFrom(baseTs, viewAnimation));
            viewAnimation.setTsTo(viewSpecificationProcessor.getViewAnimationTsTo(baseTs, viewAnimation));
            viewAnimation.setLastProjectVersionID(lastProjectVersionId);

            List<ViewFigure> animationFigures = viewSpecificationProcessor.createViewFigures(frameFileVersions, viewAnimation);

            viewAnimation.setViewFigureList(animationFigures);
            viewAnimationList.add(viewAnimation);
        }
    }

    private void composeMusic() {
        final int TIMESTAMP_ANIMATION = 1000;

        List<MusicSheet.Measure> commitsMeasures = new ArrayList<>();

        List<MusicSheet.Measure> addedLinesMeasures = new ArrayList<>();
        List<MusicSheet.Measure> deletedLinesMeasures = new ArrayList<>();

        List<MusicSheet.Measure> addedFilesMeasures = new ArrayList<>();
        List<MusicSheet.Measure> deletedFilesMeasures = new ArrayList<>();
        List<MusicSheet.Measure> changedFilesMeasures = new ArrayList<>();
        List<MusicSheet.Measure> renamedFilesMeasures = new ArrayList<>();
        List<MusicSheet.Measure> movedFilesMeasures = new ArrayList<>();


        int ts = 0;
        for (ViewAnimation viewAnimation : viewAnimationList) {

            int tempo = viewAnimation.getRepresentedEntities().size();

            List<FileVersion> animationFileVersions = viewAnimation.getRepresentedEntities().parallelStream()
                    .map(ProjectVersion::getFileVersions)
                    .flatMap(Collection::stream)
                    .toList();

            // Here we extract metrics from viewAnimation

            AtomicInteger addedLines = new AtomicInteger(0);
            AtomicInteger removedLines = new AtomicInteger(0);

            AtomicInteger deletedFiles = new AtomicInteger(0);
            AtomicInteger addedFiles = new AtomicInteger(0);
            AtomicInteger changedFiles = new AtomicInteger(0);
            AtomicInteger renamedFiles = new AtomicInteger(0);
            AtomicInteger movedFiles = new AtomicInteger(0);

            animationFileVersions.forEach(fileVersion -> {
                addedLines.addAndGet(fileVersion.getChange().getLinesAdded());
                removedLines.addAndGet(fileVersion.getChange().getLinesDeleted());

                if (fileVersion.getChange().isAdd()) {
                    addedFiles.incrementAndGet();
                } else if (fileVersion.getChange().isDelete()) {
                    deletedFiles.incrementAndGet();
                } else if (fileVersion.getChange().isModify()) {
                    changedFiles.incrementAndGet();
                } else if (fileVersion.getChange().isMove()) {
                    movedFiles.incrementAndGet();
                } else if (fileVersion.getChange().isRename()) {
                    renamedFiles.incrementAndGet();
                }
            });

            commitsMeasures.add(new MusicSheet.Measure(tempo, ts, viewAnimation.getRepresentedEntities().size(), viewAnimation.getRepresentedEntities().size()));
            addedLinesMeasures.add(new MusicSheet.Measure(tempo, ts, addedLines.get(), addedLines.get()));
            deletedLinesMeasures.add(new MusicSheet.Measure(tempo, ts, removedLines.get(), removedLines.get()));

            addedFilesMeasures.add(new MusicSheet.Measure(tempo, ts, addedFiles.get(), addedFiles.get()));
            deletedFilesMeasures.add(new MusicSheet.Measure(tempo, ts, deletedFiles.get(), deletedFiles.get()));
            changedFilesMeasures.add(new MusicSheet.Measure(tempo, ts, changedFiles.get(), changedFiles.get()));
            movedFilesMeasures.add(new MusicSheet.Measure(tempo, ts, movedFiles.get(), movedFiles.get()));
            renamedFilesMeasures.add(new MusicSheet.Measure(tempo, ts, renamedFiles.get(), renamedFiles.get()));



            ts += TIMESTAMP_ANIMATION;
        }

        musicSheets.add(new MusicSheet(standardizeMeasures(commitsMeasures, 60, 180, 40, 40, 0.3, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(addedLinesMeasures, 60, 180, 70, 70, 0, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(deletedLinesMeasures, 60, 180, 50, 50, 0, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(addedFilesMeasures, 60, 180, 50, 50, 0, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(deletedFilesMeasures, 60, 180, 50, 50, 0, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(changedFilesMeasures, 60, 180, 50, 50, 0, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(renamedFilesMeasures, 60, 180, 50, 50, 0, 1)));
        musicSheets.add(new MusicSheet(standardizeMeasures(movedFilesMeasures, 60, 180, 50, 50, 0, 1)));
    }


    private List<MusicSheet.Measure> standardizeMeasures(List<MusicSheet.Measure> measureList, final int MIN_BPM, final int MAX_BPM, final int MIN_NOTE, final int MAX_NOTE, final double MIN_AMP, final double MAX_AMP) {

        List<MusicSheet.Measure> standardizedMeasures = new ArrayList<>();
        final int DIFF_BPM = MAX_BPM - MIN_BPM;
        OptionalInt maxTempo = measureList.parallelStream().mapToInt(MusicSheet.Measure::tempo).max();
        OptionalInt minTempo = measureList.parallelStream().mapToInt(MusicSheet.Measure::tempo).min();

        final int DIFF_NOTE = MAX_NOTE - MIN_NOTE;
        OptionalInt maxNote = measureList.parallelStream().mapToInt(MusicSheet.Measure::note).max();
        OptionalInt minNote = measureList.parallelStream().mapToInt(MusicSheet.Measure::note).min();

        final double DIFF_AMP = MAX_AMP - MIN_AMP;
        List<Double> amplitudes = measureList.parallelStream().map(MusicSheet.Measure::amplitude).sorted().toList();
        int percentile = 95;
        int percentileIndex = (int) Math.ceil((percentile / 100.0) * amplitudes.size());
        percentileIndex = percentileIndex >= amplitudes.size() ? amplitudes.size() - 1 : percentileIndex;
        OptionalDouble maxAmp = OptionalDouble.of(amplitudes.get(percentileIndex));
        OptionalDouble minAmp = OptionalDouble.of(amplitudes.get(0));

        if (maxTempo.isPresent() && minTempo.isPresent() && maxNote.isPresent() && minNote.isPresent() && maxAmp.isPresent() && minAmp.isPresent()) {
            int diffTempo = maxTempo.getAsInt() - minTempo.getAsInt();
            int diffNote = maxNote.getAsInt() - minNote.getAsInt();
            double diffAmp = maxAmp.getAsDouble() - minAmp.getAsDouble();

            for (MusicSheet.Measure changesMeasure : measureList) {
                double normalizedTempo = (double) (changesMeasure.tempo() - minTempo.getAsInt()) / diffTempo;
                int standardizedTempo = (int) Math.round((normalizedTempo * DIFF_BPM) + MIN_BPM);

                double normalizedNote = (double) (changesMeasure.note() - minNote.getAsInt()) / diffNote;
                int standardizedNote = (int) Math.round((normalizedNote * DIFF_NOTE) + MIN_NOTE);

                double normalizedAmplitude = (changesMeasure.amplitude() - minAmp.getAsDouble()) / diffAmp;
                double standardizedAmplitude = (normalizedAmplitude * DIFF_AMP) + MIN_AMP;
                if (standardizedAmplitude > MAX_AMP) standardizedAmplitude = MAX_AMP;

                standardizedMeasures.add(new MusicSheet.Measure(standardizedTempo, changesMeasure.timestamp(), standardizedNote, standardizedAmplitude));
            }
        }

        return standardizedMeasures;
    }


    private List<FileVersion> getLastFHFileVersion(final List<ProjectVersion> projectVersions, final Map<FileHistory, FileVersion> fileHistoryLastFileVersionCache) {
        var fileVersionsList = projectVersions
                .stream()
                .flatMap(fV -> fV.getFileVersions().stream())
                .sorted()
                .toList();

        fileVersionsList
                .stream()
                .forEachOrdered(fV -> fileHistoryLastFileVersionCache.put(fV.getFileHistory(), fV));

        return fileHistoryLastFileVersionCache.values().stream().sorted().toList();
    }


}