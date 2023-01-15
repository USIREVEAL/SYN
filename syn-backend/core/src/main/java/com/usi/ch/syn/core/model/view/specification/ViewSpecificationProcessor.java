package com.usi.ch.syn.core.model.view.specification;

import com.usi.ch.syn.core.mapper.MapperException;
import com.usi.ch.syn.core.mapper.MapperStrategy;
import com.usi.ch.syn.core.mapper.MetricMapper;
import com.usi.ch.syn.core.mapper.strategies.*;
import com.usi.ch.syn.core.model.CodeEntity;
import com.usi.ch.syn.core.model.Metric;
import com.usi.ch.syn.core.model.change.*;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.model.view.*;
import com.usi.ch.syn.core.model.view.layout.OutwardSpiralLayout;
import com.usi.ch.syn.core.model.view.layout.PositionLayout;
import com.usi.ch.syn.core.utils.ColorUtil;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewSpecificationProcessor {

    private final ViewSpecification viewSpecification;
    private final ProjectHistory projectHistory;

    private final MetricMapper mapper;
    private final Map<Class<? extends Change>, List<Color>> changeColorMap;
    private final PositionLayout positionLayout;

    private final long lastProjectVersionTs;

    public ViewSpecificationProcessor(ViewSpecification viewSpecification, ProjectHistory projectHistory) {
        this.viewSpecification = viewSpecification;
        this.projectHistory = projectHistory;

        this.changeColorMap = generateColorMap();
        this.mapper = computeMapper();

        this.positionLayout = new OutwardSpiralLayout(viewSpecification.figureSize, viewSpecification.figureSpacing);
        positionLayout.precomputeLayout(projectHistory);

        this.lastProjectVersionTs = projectHistory.getProjectVersions().get(projectHistory.getProjectVersions().size() - 1).getTimestamp();

    }

    /**
     * This function will compute the position of Figures inside a view
     *
     * @return A map that indicates the position of each FileHistory
     */
    public Map<FileHistory, FigurePosition> computeFigurePosition() {
        Map<FileHistory, FigurePosition> fileHistoryPositionMap = new HashMap<>();

        int fileHistoriesCount = projectHistory.getFileHistories().size();
        FigurePosition direction = new FigurePosition(viewSpecification.figureSize + viewSpecification.figureSpacing, 0, 0);
        FigurePosition currentPosition = new FigurePosition(0, 0, 0);
        int segmentLength = 1;
        int segmentComputed = 0;

        for (int i = 1; i <= fileHistoriesCount; i++) {
            currentPosition = currentPosition.add(direction);
            segmentComputed++;

            fileHistoryPositionMap.put(projectHistory.getFileHistoryByID(i), currentPosition);

            if (segmentComputed == segmentLength) {
                segmentComputed = 0;
                direction = new FigurePosition(-direction.z(), 0, direction.x());
                if (direction.x() == 0 || direction.z() == 0) {
                    segmentLength++;
                }
            }
        }

        return fileHistoryPositionMap;
    }

    /**
     * This function will generate a color map, based on the chosen color, for each Change type.
     *
     * @return A map that maps each color to a list of colors (each item of the list represents a particular age).
     */
    public Map<Class<? extends Change>, List<Color>> generateColorMap() {
        Map<Class<? extends Change>, List<Color>> colorMap = new HashMap<>();

        Color baseColor = viewSpecification.colorPalette.getBaseColor();
        int agingSteps = viewSpecification.agingSteps;

        colorMap.put(FileAddition.class, ColorUtil.getColorLinearGradient(viewSpecification.colorPalette.getAddColor(), baseColor, agingSteps));
        colorMap.put(FileRenaming.class, ColorUtil.getColorLinearGradient(viewSpecification.colorPalette.getRenameColor(), baseColor, agingSteps));
        colorMap.put(FileModification.class, ColorUtil.getColorLinearGradient(viewSpecification.colorPalette.getModifyColor(), baseColor, agingSteps));
        colorMap.put(FileMoving.class, ColorUtil.getColorLinearGradient(viewSpecification.colorPalette.getMoveColor(), baseColor, agingSteps));
        colorMap.put(FileDeletion.class, ColorUtil.getColorLinearGradient(viewSpecification.colorPalette.getDeleteColor(), baseColor, agingSteps));

        return colorMap;
    }

    /**
     * This function computes the mapper to get the height of ViewFigures
     *
     * @return A MetricMapper that, given a Metric will return the mapper height.
     */
    public MetricMapper computeMapper() {
        if (viewSpecification.isMapperEnabled()) {
            Function<CodeEntity, Metric> fileVersionToSelectedMetricValue = fileVersion -> fileVersion
                    .getMetrics()
                    .stream()
                    .filter(metric -> metric.name().equals(viewSpecification.mapperMetricName))
                    .findFirst()
                    .orElseThrow();

            final MapperStrategyOptions mapperStrategyOptions = viewSpecification.mapperStrategyOptions;
            MapperStrategy<Metric, Double> strategy = switch (viewSpecification.mapperStrategy) {
                case LINEAR_STRATEGY, DEFAULT_STRATEGY -> new LinearMapperStrategy(mapperStrategyOptions.getMaxHeight());
                case NORMALIZER_STRATEGY -> new NormalizerMapperStrategy(mapperStrategyOptions.getMaxHeight());
                case BUCKET_COUNT_STRATEGY -> new BucketCountStrategy(mapperStrategyOptions.getBuckets(), mapperStrategyOptions.getMaxHeight());
                case BUCKET_VALUE_STRATEGY -> new BucketValueStrategy(mapperStrategyOptions.getBuckets(), mapperStrategyOptions.getMaxHeight());
                case BUCKET_VALUE_LINEAR_STRATEGY -> new LinearBucketValueStrategy(mapperStrategyOptions.getBuckets(), mapperStrategyOptions.getMaxHeight());
                default -> throw new IllegalStateException("Unexpected value: " + viewSpecification.mapperStrategy);
            };

            try {
                return MetricMapper.createOnFileVersionMetric(
                        projectHistory,
                        fileVersionToSelectedMetricValue,
                        strategy
                );
            } catch (MapperException e) {
                e.printStackTrace();
            }

            return null;
        }

        return null;
    }

    /**
     * This function computes how project versions should be grouped together according to the chosen grouping strategy.
     *
     * @return A list of lists, where each list represents an animation
     */
    public List<List<ProjectVersion>> getGroupedProjectVersions() {
        GroupingStrategy versionGroupingStrategy = viewSpecification.versionGroupingStrategy;
        long versionChunkSize = viewSpecification.versionGroupingChunkSize;
        List<ProjectVersion> projectVersions = projectHistory.getProjectVersions();

        if (versionGroupingStrategy == GroupingStrategy.COMMIT_STRATEGY) {
            List<List<ProjectVersion>> groupedProjectVersions = new ArrayList<>();
            for (int i = 0; i < projectVersions.size() / versionChunkSize; i++) {
                long baseID = i * versionChunkSize;
                List<ProjectVersion> projectVersionList = new ArrayList<>();
                for (long c = 0; c < versionChunkSize; c++) {
                    if (baseID + c < projectVersions.size())
                        projectVersionList.add(projectVersions.get((int) (baseID + c)));
                }
                groupedProjectVersions.add(projectVersionList);
            }

            return groupedProjectVersions;

        } else if (versionGroupingStrategy == GroupingStrategy.TIMESTAMP_STRATEGY) {
            long timestampStartingPoint = projectVersions.get(0).getTimestamp();
            long timestampEndingPoint = projectVersions.get(projectVersions.size() - 1).getTimestamp();
            double totalNumberOfVersions = Math.ceil((timestampEndingPoint - timestampStartingPoint) / (double) versionChunkSize);

            ProjectVersion currentProjectVersion = projectVersions.get(0);
            List<List<ProjectVersion>> groupedProjectVersions = new ArrayList<>();
            for (int i = 1; i <= totalNumberOfVersions; i++) {
                long timestampFrom = timestampStartingPoint + (versionChunkSize * (i - 1));
                long timestampTo = timestampFrom + versionChunkSize;
                List<ProjectVersion> projectVersionList = new ArrayList<>();

                while (!Objects.isNull(currentProjectVersion) && currentProjectVersion.getTimestamp() <= timestampTo) {
                    projectVersionList.add(currentProjectVersion);
                    currentProjectVersion = currentProjectVersion.getNext();
                }

                groupedProjectVersions.add(projectVersionList);
            }

            return groupedProjectVersions;
        } else {
            return null;
        }
    }

    /**
     * This function returns the age of a given fileVersion
     *
     * @param fileVersion    the fileVersion on where it should compute the age
     * @param viewAnimation  the view Animation
     * @return the age between the fileVersion and the projectVersion according to the chosen specifications
     */
    public short getFileVersionAge(final FileVersion fileVersion, final ViewAnimation viewAnimation) {
        short age;

        if (GroupingStrategy.TIMESTAMP_STRATEGY.equals(viewSpecification.agingGroupingStrategy)) {
            long tsDiff = viewAnimation.getTsTo() - fileVersion.getParentProjectVersion().getTimestamp();
            age = (short) (tsDiff / viewSpecification.agingStepSize);
        } else {
            long lastProjectVersionID = viewAnimation.getLastProjectVersionID();
            long figureCommitID = fileVersion.getParentProjectVersion().getId();

            age = (short) ((lastProjectVersionID - figureCommitID) / viewSpecification.agingStepSize);
        }

        if (age >= viewSpecification.agingSteps) {
            age = (short) (viewSpecification.agingSteps - 1);
        }

        if (age < 0)
            age = 0;

        return age;
    }

    public String getFileVersionShape(final FileVersion fileVersion) {
        Set<String> fileTypes = fileVersion.getFileHistory().getFileTypes();
        for (String fileType : fileTypes) {
            if (viewSpecification.fileTypeShape.containsKey(fileType)) {
                return viewSpecification.fileTypeShape.get(fileType);
            }
        }

        return "";

    }

    public double getFileVersionOpacity(FileVersion fileVersion) {
        Set<String> fileTypes = fileVersion.getFileHistory().getFileTypes();
        double maxOpacity = 0;
        for (String fileType : fileTypes) {
            if (viewSpecification.fileTypeOpacity.containsKey(fileType)) {
               double opacity = viewSpecification.fileTypeOpacity.get(fileType);
               if (opacity > maxOpacity) {
                   maxOpacity = opacity;
               }
            }
        }

        return maxOpacity;
    }

    public long getViewAnimationTsFrom(long baseTs, ViewAnimation viewAnimation) {
        if (viewSpecification.versionGroupingStrategy.equals(GroupingStrategy.TIMESTAMP_STRATEGY)) {
            return baseTs + (viewSpecification.versionGroupingChunkSize * (viewAnimation.getId() - 1));
        } else {
            return viewAnimation.getRepresentedEntities().get(0).getTimestamp();
        }
    }

    public long getViewAnimationTsTo(long baseTs, ViewAnimation viewAnimation) {

        if (viewSpecification.versionGroupingStrategy.equals(GroupingStrategy.TIMESTAMP_STRATEGY)) {
            long ts = baseTs + (viewSpecification.versionGroupingChunkSize * viewAnimation.getId());
            if (ts > lastProjectVersionTs) {
                ts = lastProjectVersionTs;
            }
            return ts;
        } else {
            return viewAnimation.getRepresentedEntities().get(viewAnimation.getRepresentedEntities().size() - 1).getTimestamp();
        }
    }

    /**
     * This function will return a list of ViewFigure ready to be displayed
     *
     * @param fileVersionList         A list of FileVersion that will be used to create ViewFigures
     * @param viewAnimation The view animation
     * @return
     */
    public List<ViewFigure> createViewFigures(final List<FileVersion> fileVersionList, final ViewAnimation viewAnimation) {
        List<ViewFigure> viewFigureList = new ArrayList<>();
        for (FileVersion fileVersion : fileVersionList) {

            if (!viewSpecification.showDeletedEntities && fileVersion.getChange().isDelete())
                continue;

            short age = getFileVersionAge(fileVersion, viewAnimation);
            Color color = changeColorMap.get(fileVersion.getChange().getClass()).get(age);

            if (fileVersion.getChange().getClass().equals(FileDeletion.class)) {
                System.out.println("WTF!!!");
            }

            double height = 0;
            boolean enabled = true;
            if (viewSpecification.isMapperEnabled()) {
                try {
                    Metric fileVersionMapperMetric = mapper.getMetricExtractor().apply(fileVersion);
                    height = mapper.getDictionary().get(fileVersionMapperMetric);
                } catch (NoSuchElementException e) {
                    enabled = false;
                    if (!viewSpecification.showUnmappedEntities)
                        continue;
                }
            }

            String shape = getFileVersionShape(fileVersion);
            double opacity = getFileVersionOpacity(fileVersion);

            int size = viewSpecification.figureSize;

            ViewFigure viewFigure = new ViewFigure(fileVersion, color, height, shape, age, enabled, opacity, size);
            viewFigureList.add(viewFigure);
        }

        positionLayout.applyLayout(viewFigureList);

        if (viewSpecification.withGround) {
            ViewFigure ground = new ViewFigure(projectHistory.getProject(), positionLayout.getGroundSize());
            viewFigureList.add(0, ground);
        }

        return viewFigureList;
    }

    public MusicSheet getMusicSheet() {
        List<MusicSheet.Measure> measureList = new ArrayList<>();





        return new MusicSheet(measureList);
    }
}
