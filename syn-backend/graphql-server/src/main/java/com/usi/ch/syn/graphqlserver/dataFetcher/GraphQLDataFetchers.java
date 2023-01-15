package com.usi.ch.syn.graphqlserver.dataFetcher;

import com.usi.ch.syn.analyzer.ProjectAnalyzer;
import com.usi.ch.syn.core.metric.FileMetricCalculator;
import com.usi.ch.syn.core.model.analysis.FileTypeManager;
import com.usi.ch.syn.core.model.history.FileHistory;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.model.view.View;
import com.usi.ch.syn.core.model.view.ViewAnimation;
import com.usi.ch.syn.core.model.view.specification.*;
import com.usi.ch.syn.core.storage.ProjectFactory;
import com.usi.ch.syn.core.utils.ProjectFileIdentifier;
import com.usi.ch.syn.graphqlserver.dto.FileTypeCounterDTO;
import com.usi.ch.syn.graphqlserver.dto.FileTypeMetricsDTO;
import com.usi.ch.syn.graphqlserver.dto.ViewAnimationDTO;
import com.usi.ch.syn.graphqlserver.dto.ViewDTO;
import graphql.schema.DataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GraphQLDataFetchers {
    Logger logger = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    public DataFetcher<Project> createNewProject() {
        return dataFetchingEnvironment -> {
            String projectName = dataFetchingEnvironment.getArgument("projectName");
            String projectLocation = dataFetchingEnvironment.getArgument("projectLocation");

            Project projectOptional = ProjectFactory.getInstance().createProject(projectName, projectLocation);
            return Optional.ofNullable(projectOptional).orElse(null);
        };
    }

    public DataFetcher<Project> getProjectByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);
            return projectOptional.orElse(null);
        };
    }

    public DataFetcher<ProjectHistory> getProjectHistoryDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, ProjectHistory> projectSource = dataFetchingEnvironment.getSource();
            return projectSource.get("projectHistory");
        };
    }

    public DataFetcher<List<ProjectFileIdentifier>> getProjectListDataFetcher() {
        return dataFetchingEnvironment -> {
            return ProjectFactory.getInstance().getListAvailableProjects();
        };
    }

    public DataFetcher<ViewDTO> getViewDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Map<String, Object> viewSpecificationMap = dataFetchingEnvironment.getArgument("viewSpecification");
            ViewSpecification viewSpecification = getViewSpecification(viewSpecificationMap);
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                long startTs = System.currentTimeMillis();
                logger.info("Starting to build a new view for project {}", project.getName());
                View view = new View(viewSpecification, project.getProjectHistory());
                logger.info("Built a view for project {} in {}s", project.getName(), (System.currentTimeMillis() - startTs) / 1000);
                ViewDTO vsd =  new ViewDTO(view);
                return vsd;
            }

            return null;
        };
    }

    public DataFetcher<ViewDTO> getPartialViewDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Map<String, Object> viewSpecificationMap = dataFetchingEnvironment.getArgument("viewSpecification");
            ViewSpecification viewSpecification = getViewSpecification(viewSpecificationMap);
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                long startTs = System.currentTimeMillis();
                logger.info("Starting to build a new view for project {}", project.getName());
                View view = new View(viewSpecification, project.getProjectHistory());
                logger.info("Built a view for project {} in {}s", project.getName(), (System.currentTimeMillis() - startTs) / 1000);
                Integer skip = dataFetchingEnvironment.getArgument("viewAnimationId");
                List<ViewAnimation> partialViewAnimationList = view.getViewAnimationList().stream().skip(skip).limit(100).toList();
                return new ViewDTO(view.getViewAnimationList().size(), partialViewAnimationList, view.getMusicSheets());
            }

            return null;
        };
    }

    private ViewSpecification getViewSpecification(Map<String, Object> viewSpecificationMap) {
        ColorPalette colorPalette = ColorPalette.DEFAULT;
        if (viewSpecificationMap.containsKey("colorPalette")) {
            Map<String, String> colorPaletteMap = (Map) viewSpecificationMap.get("colorPalette");
            colorPalette = new ColorPalette(
                    Color.decode(colorPaletteMap.get("addColor")),
                    Color.decode(colorPaletteMap.get("deleteColor")),
                    Color.decode(colorPaletteMap.get("renameColor")),
                    Color.decode(colorPaletteMap.get("moveColor")),
                    Color.decode(colorPaletteMap.get("modifyColor")),
                    Color.decode(colorPaletteMap.get("baseColor")))
            ;
        }

        MapperStrategyOptions mapperStrategyOptions = null;
        if (viewSpecificationMap.containsKey("mapperOptions")) {
            Map<String, Object> mapperOptionsMap = (Map) viewSpecificationMap.get("mapperOptions");
            mapperStrategyOptions = new MapperStrategyOptions();

            mapperStrategyOptions.setMaxHeight((Integer) mapperOptionsMap.get("maxHeight"));
            mapperStrategyOptions.setBuckets((Integer) mapperOptionsMap.get("buckets"));
        }

        Map<String, String> fileTypeShape = new HashMap<>();
        if (viewSpecificationMap.containsKey("fileTypeShape")) {
            List<Map<String, String>> fileTypeShapeInput = (List<Map<String, String>>) viewSpecificationMap.get("fileTypeShape");
            for (Map<String, String> stringStringMap : fileTypeShapeInput) {
                fileTypeShape.put(stringStringMap.get("fileType"), stringStringMap.get("shapeName"));
            }

        }

        Map<String, Double> fileTypeOpacity = new HashMap<>();
        if (viewSpecificationMap.containsKey("fileTypeOpacity")) {
            List<Map> fileTypeShapeInput = (List<Map>) viewSpecificationMap.get("fileTypeOpacity");
            for (Map stringStringMap : fileTypeShapeInput) {
                fileTypeOpacity.put((String) stringStringMap.get("fileType"), (Double) stringStringMap.get("opacity"));
            }

        }

        return ViewSpecification
                .builder()
                .versionGroupingStrategy(GroupingStrategy.valueOf((String) viewSpecificationMap.get("versionGroupingStrategy")))
                .versionGroupingChunkSize((Integer) viewSpecificationMap.get("versionGroupingChunkSize"))
                .colorPalette(colorPalette).agingGroupingStrategy(GroupingStrategy.valueOf((String) viewSpecificationMap.get("agingGroupingStrategy")))
                .agingStepSize((Integer) viewSpecificationMap.get("agingStepSize")).agingSteps((Integer) viewSpecificationMap.get("agingSteps"))
                .mapperStrategyOptions(mapperStrategyOptions).mapperStrategy(MapperStrategyName.valueOf((String) viewSpecificationMap.get("mapperStrategy")))
                .mapperMetricName((String) viewSpecificationMap.get("mapperMetricName")).figureSize((Integer) viewSpecificationMap.get("figureSize"))
                .figureSpacing((Integer) viewSpecificationMap.get("figureSpacing"))
                .fileTypeShape(fileTypeShape)
                .fileTypeOpacity(fileTypeOpacity)
                .showDeletedEntities((Boolean) viewSpecificationMap.get("showDeletedEntities"))
                .showUnmappedEntities((Boolean) viewSpecificationMap.get("showUnmappedEntities"))
                .build();
    }

    public DataFetcher<List<ViewAnimationDTO>> getViewAnimationListDataFetcher() {
        return dataFetchingEnvironment -> {
            ViewDTO view = dataFetchingEnvironment.getSource();
            return view.getViewAnimationList().parallelStream().map(ViewAnimationDTO::new).toList();
        };
    }

    public DataFetcher<FileHistory> getFileHistoryDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Integer fileHistoryId = dataFetchingEnvironment.getArgument("fileHistoryId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);

            return projectOptional.map(project -> project.getProjectHistory().getFileHistoryByID(fileHistoryId)).orElse(null);
        };
    }

    public DataFetcher<ProjectVersion> getProjectVersionDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Integer projectVersionId = dataFetchingEnvironment.getArgument("projectVersionId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);

            return projectOptional.map(project -> project.getProjectHistory().getProjectVersionByID(projectVersionId)).orElse(null);
        };
    }

    public DataFetcher<List<ProjectVersion>> getProjectVersionsDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            List<Integer> projectVersionsId = dataFetchingEnvironment.getArgument("projectVersionsId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);
            if (projectOptional.isPresent()) {
                List<ProjectVersion> projectVersions = new ArrayList<>();
                Project project = projectOptional.get();
                for (Integer projectVersionId : projectVersionsId) {
                    projectVersions.add(project.getProjectHistory().getProjectVersionByID(projectVersionId));
                }
                return projectVersions;
            }
            return null;
        };
    }

    public DataFetcher<Integer> getGroupingPreviewDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                Map<String, Object> viewSpecificationMap = dataFetchingEnvironment.getArgument("viewSpecification");
                ViewSpecification viewSpecification = getViewSpecification(viewSpecificationMap);

                ViewSpecificationProcessor viewSpecificationProcessor = new ViewSpecificationProcessor(viewSpecification, project.getProjectHistory());
                return viewSpecificationProcessor.getGroupedProjectVersions().size();

            }

            return null;
        };
    }

    public DataFetcher<List<FileTypeCounterDTO>> getFileTypeCounterDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                return project.getProjectHistory().getFileHistoryFileTypeCount().entrySet().parallelStream().map(stringLongEntry -> new FileTypeCounterDTO(stringLongEntry.getKey(), stringLongEntry.getValue())).toList();
            }

            return null;
        };
    }

    public DataFetcher getFileTypeMetricsDataFetcher() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("projectId");
            Optional<Project> projectOptional = ProjectFactory.getInstance().getProject(projectId);


            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                List<String> fileTypeFilter = dataFetchingEnvironment.getArgument("fileTypeFilter");

                if (fileTypeFilter.isEmpty()) {
                    fileTypeFilter = project.getProjectHistory().getFileHistoryFileTypes().stream().toList();
                }

                Map<String, Set<String>> fileTypeMetricsMap = fileTypeFilter.stream().collect(Collectors.groupingBy(fileType -> fileType, Collectors.flatMapping(fileType -> FileTypeManager.typeMetricMap.getOrDefault(fileType, List.of()).stream().map(FileMetricCalculator::name), Collectors.toSet())));


                return fileTypeMetricsMap.entrySet().stream().map(e -> new FileTypeMetricsDTO(e.getKey(), e.getValue())).toList();


            }

            return null;
        };
    }
}