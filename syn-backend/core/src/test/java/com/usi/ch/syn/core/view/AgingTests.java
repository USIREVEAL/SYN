package com.usi.ch.syn.core.view;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.model.view.View;
import com.usi.ch.syn.core.model.view.ViewAnimation;
import com.usi.ch.syn.core.model.view.ViewFigure;
import com.usi.ch.syn.core.model.view.specification.*;
import com.usi.ch.syn.core.storage.ProjectFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AgingTests {

    //    private static final Logger logger = LogManager.getLogger(ViewTests.class);
    final static String SMALL_PROJECT_NAME = "JetUML";
    final static String SMALL_PROJECT_PATH = "https://github.com/prmr/JetUML.git";
    private static final ProjectFactory projectFactory = ProjectFactory.getInstance();
    private static Project project;


    @Test
    public void textAgingOneCommit() {
        Project project = projectFactory.getProject(1).get();

        MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
        mapperStrategyOptions.setBuckets(100);

        ViewSpecification viewSpecification = ViewSpecification.builder()
                .versionGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                .versionGroupingChunkSize(1)
                .colorPalette(ColorPalette.DEFAULT)
                .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                .agingStepSize(1)
                .agingSteps(2)
                .mapperStrategy(MapperStrategyName.BUCKET_COUNT_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName("SLOC")
                .fileTypeShape(Map.of("JAVA", "BOX"))
                .fileTypeOpacity(Map.of("JAVA", 1.0))
                .showDeletedEntities(true)
                .withGround(true)
                .showUnmappedEntities(true)
                .build();

        View view = new View(viewSpecification, project.getProjectHistory());

        List<Integer> fileHistoryIDS = new ArrayList<>();

        for (ViewAnimation viewAnimation : view.getViewAnimationList()) {
            fileHistoryIDS = viewAnimation.getRepresentedEntities().parallelStream().flatMap(pv -> pv.getFileVersions().stream()).map(FileVersion::getFileHistory).map(FileHistory::getId).toList();

            for (ViewFigure viewFigure : viewAnimation.getViewFigureList().stream().skip(1).toList()) {
                if (fileHistoryIDS.contains(viewFigure.getFileHistoryId())) {
                    assertEquals(0, viewFigure.getAge());
                } else {
                    assertEquals(1, viewFigure.getAge());
                }
            }


        }
    }


    @Test
    public void testAgingTwoGroupingStrategies() {
        Project project = projectFactory.getProject(1).get();

        MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
        mapperStrategyOptions.setBuckets(100);

        ViewSpecification viewSpecification = ViewSpecification.builder()
                .versionGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                .versionGroupingChunkSize(1)
                .colorPalette(ColorPalette.DEFAULT)
                .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                .agingStepSize(1)
                .agingSteps(2)
                .mapperStrategy(MapperStrategyName.BUCKET_COUNT_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName("SLOC")
                .fileTypeShape(Map.of("JAVA", "BOX"))
                .fileTypeOpacity(Map.of("JAVA", 1.0))
                .showDeletedEntities(true)
                .withGround(true)
                .showUnmappedEntities(true)
                .build();

        View viewOneCommit = new View(viewSpecification, project.getProjectHistory());

        ViewSpecification viewSpecification2 = ViewSpecification.builder()
                .versionGroupingStrategy(GroupingStrategy.TIMESTAMP_STRATEGY)
                .versionGroupingChunkSize(2629743)
                .colorPalette(ColorPalette.DEFAULT)
                .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                .agingStepSize(1)
                .agingSteps(2)
                .mapperStrategy(MapperStrategyName.BUCKET_COUNT_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName("SLOC")
                .fileTypeShape(Map.of("JAVA", "BOX"))
                .fileTypeOpacity(Map.of("JAVA", 1.0))
                .showDeletedEntities(true)
                .withGround(true)
                .showUnmappedEntities(true)
                .build();

        View viewOneMonth = new View(viewSpecification2, project.getProjectHistory());

        for (ViewAnimation viewAnimation : viewOneMonth.getViewAnimationList()) {
            int representedEntitiesSize = viewAnimation.getRepresentedEntities().size();
            if (representedEntitiesSize > 0) {
                ProjectVersion projectVersion = viewAnimation.getRepresentedEntities().get(representedEntitiesSize - 1);

                Optional<ViewAnimation> viewAnimationOptional = viewOneCommit.getViewAnimationList().stream().filter(viewAnimation1 -> viewAnimation1.getRepresentedEntities().get(0).equals(projectVersion)).findFirst();

                if (viewAnimationOptional.isPresent()) {
                    ViewAnimation oneCommitViewAnimation = viewAnimationOptional.get();

                    for (int i = 0; i < oneCommitViewAnimation.getViewFigureList().size(); i++) {
                        ViewFigure oneCommitViewFigure = oneCommitViewAnimation.getViewFigureList().get(i);
                        ViewFigure oneMonthViewFigure = viewAnimation.getViewFigureList().get(i);

                        if (oneCommitViewFigure.getAge() != (oneMonthViewFigure.getAge())) {
                            System.out.println("aaa");
                        }

                        assertEquals(oneCommitViewFigure.getAge(), oneMonthViewFigure.getAge());
                    }
                }
            }
        }
    }
}
