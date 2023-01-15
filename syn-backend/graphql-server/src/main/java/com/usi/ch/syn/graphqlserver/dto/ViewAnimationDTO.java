package com.usi.ch.syn.graphqlserver.dto;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.model.view.FigurePosition;
import com.usi.ch.syn.core.model.view.ViewAnimation;
import com.usi.ch.syn.core.model.view.ViewFigure;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
public class ViewAnimationDTO {

    public class DebugStatistics {

        int commits;
        int linesAdded;
        int linesRemoved;
        int filesAdded;
        int filesRemoved;
        int filesChanged;
        int filesRenamed;
        int filesMoved;

        public DebugStatistics (ViewAnimation viewAnimation) {
            AtomicInteger linesAdded = new AtomicInteger(0);
            AtomicInteger linesRemoved = new AtomicInteger(0);
            AtomicInteger filesAdded = new AtomicInteger(0);
            AtomicInteger filesRemoved = new AtomicInteger(0);
            AtomicInteger filesChanged = new AtomicInteger(0);
            AtomicInteger filesRenamed = new AtomicInteger(0);
            AtomicInteger filesMoved = new AtomicInteger(0);

            viewAnimation.getRepresentedEntities().parallelStream()
                .flatMap(projectVersion -> projectVersion.getFileVersions().parallelStream())
                .map(FileVersion::getChange)
                .forEach(change -> {
                    linesAdded.addAndGet(change.getLinesAdded());
                    linesRemoved.addAndGet(change.getLinesDeleted());
                    if (change.isAdd()) {
                        filesAdded.incrementAndGet();
                    } else if (change.isDelete()) {
                        filesRemoved.incrementAndGet();
                    } else if (change.isModify()) {
                        filesChanged.incrementAndGet();
                    } else if (change.isRename()) {
                        filesRenamed.incrementAndGet();
                    } else {
                        filesMoved.incrementAndGet();
                    }
                });

            this.commits = viewAnimation.getRepresentedEntities().size();
            this.linesAdded = linesAdded.get();
            this.linesRemoved = linesRemoved.get();
            this.filesAdded = filesAdded.get();
            this.filesRemoved = filesRemoved.get();
            this.filesChanged = filesChanged.get();
            this.filesRenamed = filesRenamed.get();
            this.filesMoved = filesMoved.get();
        }
    }

    private final int id;
    private final List<Integer> projectVersionIds;
    private final List<ViewFigureDTO> viewFigures;

    private long tsFrom, tsTo;

    /* for debugging purposes */
    private final DebugStatistics debugStatistics;


    public ViewAnimationDTO(final ViewAnimation viewAnimation) {
        id = viewAnimation.getId();
        projectVersionIds = viewAnimation.getRepresentedEntities().stream().map(Entity::getId).toList();
        debugStatistics = new DebugStatistics(viewAnimation);
        viewFigures = viewAnimation.getViewFigureList().parallelStream().map(ViewFigureDTO::new).toList();
        tsFrom = viewAnimation.getTsFrom();
        tsTo = viewAnimation.getTsTo();
    }
}
