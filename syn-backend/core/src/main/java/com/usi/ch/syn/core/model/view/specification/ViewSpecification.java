package com.usi.ch.syn.core.model.view.specification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Builder
@ToString
public class ViewSpecification {
    // VERSION GROUPING
    @Builder.Default
    public final GroupingStrategy versionGroupingStrategy = GroupingStrategy.COMMIT_STRATEGY;

    @Builder.Default
    public final int versionGroupingChunkSize = 1;

    // AGING
    @Builder.Default
    public final ColorPalette colorPalette = ColorPalette.DEFAULT;

    @Builder.Default
    public final GroupingStrategy agingGroupingStrategy = GroupingStrategy.COMMIT_STRATEGY;

    @Builder.Default
    public final int agingStepSize = 5;

    @Builder.Default
    public final int agingSteps = 10;

    // MAPPER
    @Builder.Default
    public final MapperStrategyOptions mapperStrategyOptions = new MapperStrategyOptions();

    @Builder.Default
    public final MapperStrategyName mapperStrategy = MapperStrategyName.NONE;

    @Builder.Default
    public final String mapperMetricName = "";

    @Builder.Default
    public boolean showUnmappedEntities = true;

    // SHAPE
    @Builder.Default
    public final Map<String, String> fileTypeShape = new HashMap<>();

    // OPACITY
    @Builder.Default
    public final Map<String, Double> fileTypeOpacity = new HashMap<>();

    // VISUALIZATION
    @Builder.Default
    public int figureSize = 5;

    @Builder.Default
    public int figureSpacing = 5;

    @Builder.Default
    public boolean showDeletedEntities = false;

    // Ground
    @Builder.Default
    public boolean withGround = true;



    public boolean isMapperEnabled() {
        return !MapperStrategyName.NONE.equals(mapperStrategy);
    }
}
