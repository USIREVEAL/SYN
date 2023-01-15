package com.usi.ch.syn.core.model.version;

import com.usi.ch.syn.core.model.EntityIdTranslator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represent a commit/version of the repo
 */
@Getter
@EntityIdTranslator.IdClassIdentifier(value = 2)
public class ProjectVersion extends Version<ProjectVersion> {

    private final long timestamp;
    private final String commitHash;
    private final String commitMessage;
    protected List<FileVersion> fileVersions = new ArrayList<>();


    public ProjectVersion(int id, long timestamp, String commitHash, String commitMessage) {
        super(id);
        this.timestamp = timestamp;
        this.commitHash = commitHash;
        this.commitMessage = commitMessage;
    }

    public void addFileVersion(final FileVersion fileVersion) {
        fileVersion.setParentProjectVersion(this);
        fileVersions.add(fileVersion);
    }

    public int getTotalNumberOfChanges() {
        return fileVersions.parallelStream().mapToInt(fileVersion -> fileVersion.getChange().getLinesAdded() + fileVersion.getChange().getLinesDeleted()).sum();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectVersion)) return false;
        if (!super.equals(o)) return false;
        ProjectVersion that = (ProjectVersion) o;
        return getTimestamp() == that.getTimestamp() && Objects.equals(getCommitHash(), that.getCommitHash()) && Objects.equals(getCommitMessage(), that.getCommitMessage()) && Objects.equals(getFileVersions(), that.getFileVersions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTimestamp(), getCommitHash(), getCommitMessage(), getFileVersions());
    }
}
