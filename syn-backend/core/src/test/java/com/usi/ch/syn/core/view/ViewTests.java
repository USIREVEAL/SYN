package com.usi.ch.syn.core.view;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.model.view.View;
import com.usi.ch.syn.core.model.view.specification.*;
import com.usi.ch.syn.core.storage.ProjectFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewTests {

    //    private static final Logger logger = LogManager.getLogger(ViewTests.class);
    final static String SMALL_PROJECT_NAME = "JetUML";
    final static String SMALL_PROJECT_PATH = "https://github.com/prmr/JetUML.git";
    private static final ProjectFactory projectFactory = ProjectFactory.getInstance();
    private static Project project;

    @BeforeAll
    public static void initializeProject() {
        project = projectFactory.getProject(2).get();
    }

    @AfterAll
    public static void removeProject() {
//        projectFactory.deleteProject(project.getId());
    }

    @Test
    public void testExtensiveCommitGroupingNumber() {

        List<Integer> groupingTestSize = List.of(1, 2, 3, 5, 7, 9, 10, 11, 13);

        for (Integer groupingSize : groupingTestSize) {


            MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
            mapperStrategyOptions.setBuckets(100);

            ViewSpecification viewSpecification = ViewSpecification.builder()
                    .versionGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                    .versionGroupingChunkSize(groupingSize)
                    .colorPalette(ColorPalette.DEFAULT)
                    .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                    .agingStepSize(1)
                    .agingSteps(10)
                    .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                    .mapperStrategyOptions(mapperStrategyOptions)
                    .mapperMetricName("SLOC")
                    .build();

            View view = new View(viewSpecification, project.getProjectHistory());

            int projectVersionsCount = project.getProjectHistory().getProjectVersions().size();
            int animationCount = view.getViewAnimationList().size();

            assertEquals(Math.ceil(projectVersionsCount / (double) groupingSize), animationCount, "Test failed with groupingSize: " + groupingSize);
        }

    }


    @Test
    public void testExtensiveTimestampGroupingNumber() {
        List<Integer> groupingTestSize = List.of(3600, 86400, 604800, 2629743, 31556926);
        for (Integer groupingSize : groupingTestSize) {

            MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
            mapperStrategyOptions.setBuckets(100);
            ViewSpecification viewSpecification = ViewSpecification.builder()
                    .versionGroupingStrategy(GroupingStrategy.TIMESTAMP_STRATEGY)
                    .versionGroupingChunkSize(groupingSize)
                    .colorPalette(ColorPalette.DEFAULT)
                    .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                    .agingStepSize(1)
                    .agingSteps(10)
                    .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                    .mapperStrategyOptions(mapperStrategyOptions)
                    .mapperMetricName("SLOC")
                    .build();

            View view = new View(viewSpecification, project.getProjectHistory());

            long firstTs = project.getProjectHistory().getProjectVersionByID(1).getTimestamp();
            long lastTs = project.getProjectHistory().getProjectVersionByID(project.getProjectHistory().getProjectVersions().size()).getTimestamp();

            int animationCount = view.getViewAnimationList().size();

            assertEquals(Math.ceil((lastTs - firstTs) / (double) groupingSize), animationCount, "Test failed with groupingSize: " + groupingSize);
        }
    }

    @Test
    public void testExtensiveTimestampGroupingCorrectness() {
        List<Integer> groupingTestSize = List.of(3600, 86400, 604800, 2629743, 31556926);
        for (Integer groupingSize : groupingTestSize) {

            MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
            mapperStrategyOptions.setBuckets(100);
            ViewSpecification viewSpecification = ViewSpecification.builder()
                    .versionGroupingStrategy(GroupingStrategy.TIMESTAMP_STRATEGY)
                    .versionGroupingChunkSize(groupingSize)
                    .colorPalette(ColorPalette.DEFAULT)
                    .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                    .agingStepSize(1)
                    .agingSteps(10)
                    .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                    .mapperStrategyOptions(mapperStrategyOptions)
                    .mapperMetricName("SLOC")
                    .build();

            View view = new View(viewSpecification, project.getProjectHistory());

            view.getViewAnimationList().forEach(viewAnimation -> {
                int representedEntitySize = viewAnimation.getRepresentedEntities().size();
                if (representedEntitySize > 0) {
                    Entity firstEntity = viewAnimation.getRepresentedEntities().get(0);
                    Entity lastEntity = viewAnimation.getRepresentedEntities().get(representedEntitySize - 1);

                    if (firstEntity instanceof ProjectVersion firstVersion && lastEntity instanceof ProjectVersion lastVersion) {
                        long tsDelta = lastVersion.getTimestamp() - firstVersion.getTimestamp();
                        assertTrue(tsDelta <= groupingSize, "Found a group (" + viewAnimation.getId() + ") with a timestamp delta higher than " + groupingSize + "ms");
                    }
                }
            });
        }
    }

    @Test
    public void testViewFigureEnabled() {
        MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
        mapperStrategyOptions.setBuckets(100);
        final String METRIC_NAME = "SLOC";
        ViewSpecification viewSpecification = ViewSpecification.builder()
                .versionGroupingStrategy(GroupingStrategy.TIMESTAMP_STRATEGY)
                .versionGroupingChunkSize(604800)
                .colorPalette(ColorPalette.DEFAULT)
                .agingGroupingStrategy(GroupingStrategy.COMMIT_STRATEGY)
                .agingStepSize(1)
                .agingSteps(10)
                .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName(METRIC_NAME)
                .build();

        View view = new View(viewSpecification, project.getProjectHistory());
        List<FileVersion> fileVersionList = project.getProjectHistory().getAllFileVersions();
        view.getViewAnimationList().stream().flatMap(viewAnimation -> viewAnimation.getViewFigureList().stream()).forEach(viewFigure -> {
            FileVersion fileVersion = fileVersionList.get(viewFigure.getEntity().getId() - 1);
            if (fileVersion.getFileHistory().getFileTypes().contains(METRIC_NAME)) {
                assertTrue(viewFigure.isEnabled());
            }
        });
    }

    @Test
    public void testHideDeletedFileVersions() {
        MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
        mapperStrategyOptions.setBuckets(100);
        final String METRIC_NAME = "SLOC";
        ViewSpecification viewSpecification = ViewSpecification.builder()
                .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName(METRIC_NAME)
                .showDeletedEntities(false)
                .build();

        View view = new View(viewSpecification, project.getProjectHistory());
        List<FileVersion> fileVersionList = project.getProjectHistory().getAllFileVersions();
        view.getViewAnimationList().stream().flatMap(viewAnimation -> viewAnimation.getViewFigureList().stream()).forEach(viewFigure -> {
            FileVersion fileVersion = fileVersionList.get(viewFigure.getEntity().getId() - 1);
            assertFalse(fileVersion.getChange().isDelete());
        });
    }

    @Test
    public void testHideUnmappedEntities() {
        MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
        mapperStrategyOptions.setBuckets(100);
        final String METRIC_NAME = "SLOC";
        ViewSpecification viewSpecification = ViewSpecification.builder()
                .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName(METRIC_NAME)
                .showUnmappedEntities(false)
                .showDeletedEntities(false)
                .build();

        View view = new View(viewSpecification, project.getProjectHistory());
        List<FileVersion> fileVersionList = project.getProjectHistory().getAllFileVersions();
        view.getViewAnimationList().stream().flatMap(viewAnimation -> viewAnimation.getViewFigureList().stream()).forEach(viewFigure -> {
            FileVersion fileVersion = fileVersionList.get(viewFigure.getEntity().getId() - 1);
            assertTrue(viewFigure.getHeight() > 0);
        });
    }

    @Test
    public void testFileTypeOpacity() {
        MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();
        mapperStrategyOptions.setBuckets(100);
        final String METRIC_NAME = "SLOC";
        Map<String, Double> fileTypeOpacityMap = new HashMap<>();
        fileTypeOpacityMap.put("JAVA", 0.99);
        fileTypeOpacityMap.put("BINARY", 0.25);
        ViewSpecification viewSpecification = ViewSpecification.builder()
                .mapperStrategy(MapperStrategyName.BUCKET_VALUE_LINEAR_STRATEGY)
                .mapperStrategyOptions(mapperStrategyOptions)
                .mapperMetricName(METRIC_NAME)
                .showUnmappedEntities(false)
                .showDeletedEntities(false)
                .fileTypeOpacity(fileTypeOpacityMap)
                .build();

        View view = new View(viewSpecification, project.getProjectHistory());
        List<FileVersion> fileVersionList = project.getProjectHistory().getAllFileVersions();
        view.getViewAnimationList().stream().flatMap(viewAnimation -> viewAnimation.getViewFigureList().stream()).forEach(viewFigure -> {
            FileVersion fileVersion = fileVersionList.get(viewFigure.getEntity().getId() - 1);
            assertAll(
                    () -> assertTrue(!(fileVersion.getFileHistory().getFileTypes().contains("JAVA")) || viewFigure.getOpacity() == fileTypeOpacityMap.get("JAVA")),
                    () -> assertTrue(!(fileVersion.getFileHistory().getFileTypes().contains("BINARY")) || viewFigure.getOpacity() == fileTypeOpacityMap.get("BINARY"))
            );
        });
    }


}
