package com.usi.ch.syn.core.model.history;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.EntityIdTranslator;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import com.usi.ch.syn.core.utils.Pair;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the evolution matrix of a project
 */
@Getter
public class ProjectHistory extends Entity {
    private static final Logger logger = LoggerFactory.getLogger(ProjectHistory.class);

    // Evolution matrix properties
    private final List<FileHistory> fileHistories;
    private final List<ProjectVersion> projectVersions;

    private final Project project;

    public ProjectHistory(Project project, final List<FileHistory> fileHistories, final  List<ProjectVersion> projectVersions) {
        super(-1);
        this.fileHistories = fileHistories;
        this.projectVersions = projectVersions;
        this.project = project;
    }


    /**
     * This function returns a ProjectVersion searched through the ProjectHistory projectVersions list.
     * If the projectVersion cannot be located a RuntimeException is thrown.
     *
     * @param projectVersionID the id of the projectVersion you're looking for
     * @return The searched ProjectVersion
     */
    public ProjectVersion getProjectVersionByID(int projectVersionID) {
        if (projectVersions.
                get(EntityIdTranslator.getEntityOrder(projectVersionID) - 1).getId() == projectVersionID) {
            return projectVersions.get(EntityIdTranslator.getEntityOrder(projectVersionID) - 1);
        }

        Collections.sort(projectVersions);

        if (projectVersions.get(EntityIdTranslator.getEntityOrder(projectVersionID)  - 1).getId() != projectVersionID) {
            logger.error("Unable to find ProjectVersion with id {}", projectVersionID);
            throw new RuntimeException("Unable to find ProjectVersion with id " + projectVersionID);
        }

        return projectVersions.get(EntityIdTranslator.getEntityOrder(projectVersionID) );
    }

    public FileHistory getFileHistoryByID(int fileHistoryID) {
        if (fileHistories.get(EntityIdTranslator.getEntityOrder(fileHistoryID)  - 1).getId() == fileHistoryID) {
            return fileHistories.get(EntityIdTranslator.getEntityOrder(fileHistoryID)  - 1);
        }

        return null;
    }

    /**
     * This function returns all the ProjectHistory's files version
     *
     * @return A List of FileVersion
     */
    public List<FileVersion> getAllFileVersions() {
        return fileHistories.stream()
                .map(FileHistory::getFileVersions)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .toList();
    }

    public List<FileVersion> getLastFileVersionOfFileHistoryBefore(final ProjectVersion projectVersion) {

        Map<FileHistory, FileVersion> fileHistoryFileVersionMap = new HashMap<>();


        ProjectVersion versionIterator = projectVersion.getPrevious();
        while (versionIterator != null) {
            versionIterator.getFileVersions()
                    .stream()
                    .filter(fileVersion -> !fileHistoryFileVersionMap.containsKey(fileVersion.getFileHistory()))
                    .forEach(fileVersion -> {
                        fileHistoryFileVersionMap.put(fileVersion.getFileHistory(), fileVersion);
                    });

            versionIterator = versionIterator.getPrevious();
        }

        return fileHistoryFileVersionMap.values().stream().toList();
    }

    /**
     * This function searches across all the path of FileHistories for a match.
     * This research is also done on previously used paths (path before a RENAME/MOVE).
     *
     * @param searchKey A String that is part of the path
     * @return A list of FileHistory
     */
    public List<FileHistory> getFileHistoriesByString(String searchKey) {
        return fileHistories.stream()
                .parallel()
                .filter(fileHistory -> fileHistory.getAliases().stream().anyMatch(alias -> alias.contains(searchKey)))
                .toList();
    }

    /**
     * This function counts the number of occurrences of each fileType.
     *
     * @return A map where each key represents the fileType and each value represents its counter.
     */
    public Map<String, Long> getFileHistoryFileTypeCount() {
        return fileHistories
                .parallelStream()
                .flatMap(fileHistory -> fileHistory.getFileTypes().stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
    }

    /**
     * This function returns all the fileTypes found in this ProjectHistory.
     *
     * @return A Set of Strings, each one representing a fileType.
     */
    public Set<String> getFileHistoryFileTypes() {
        return fileHistories
                .parallelStream()
                .flatMap(fileHistory -> fileHistory.getFileTypes().stream())
                .collect(Collectors.toSet());
    }

    /**
     * This function groups all the FileHistories by their fileTypes.
     *
     * @return A Map where the key is a String representing a fileType and the value is
     * a List of FileHistories that share the same fileType.
     */
    public Map<String, Set<FileHistory>> getFileTypeFileHistoriesMap() {
        return fileHistories
                .parallelStream()
                .flatMap(fileHistory -> {
                    List<Pair<String, FileHistory>> tagFileHistoryList = new ArrayList<>();
                    fileHistory.getFileTypes().forEach(t -> tagFileHistoryList.add(new Pair<>(t, fileHistory)));
                    return tagFileHistoryList.stream();
                })
                .collect(
                        Collectors.groupingBy(
                                Pair::getKey,
                                Collectors.mapping(Pair::getValue,
                                        Collectors.toSet()
                                )
                        )
                );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectHistory that)) return false;
        if (!super.equals(o)) return false;
        return getFileHistories().equals(that.getFileHistories()) && getProjectVersions().equals(that.getProjectVersions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFileHistories(), getProjectVersions());
    }
}
