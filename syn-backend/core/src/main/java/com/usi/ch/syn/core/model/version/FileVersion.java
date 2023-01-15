package com.usi.ch.syn.core.model.version;

import com.usi.ch.syn.core.model.EntityIdTranslator;
import com.usi.ch.syn.core.model.change.Change;
import com.usi.ch.syn.core.model.history.FileHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@EntityIdTranslator.IdClassIdentifier(value = 4)
public class FileVersion extends Version<FileVersion> {

    private FileHistory fileHistory;
    private ProjectVersion parentProjectVersion;
    private Change change;

    public FileVersion(int id, Change change, FileHistory fileHistory) {
        super(id);
        this.change = change;
        this.fileHistory = fileHistory;
        fileHistory.getFileVersions().add(this);
    }

    public FileVersion(int id) {
        super(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileVersion)) return false;
        if (!super.equals(o)) return false;
        FileVersion that = (FileVersion) o;
        return Objects.equals(getFileHistory().getId(), that.getFileHistory().getId()) && Objects.equals(getChange(), that.getChange()) && parentProjectVersion.getId() == that.getParentProjectVersion().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFileHistory().getId(), getChange(), getParentProjectVersion().getId());
    }
}
