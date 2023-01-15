package com.usi.ch.syn.core.model.history;

import com.usi.ch.syn.core.model.CodeEntity;
import com.usi.ch.syn.core.model.EntityIdTranslator;
import com.usi.ch.syn.core.model.version.FileVersion;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;


/**
 * This class represents an entity and its history
 *
 * Alternative name - HistoryVersion
 */
@Getter
@EntityIdTranslator.IdClassIdentifier(value = 1)
public class FileHistory extends CodeEntity {


    private String name;
    private String path;
    private final List<FileVersion> fileVersions = new ArrayList<>();
    @Setter private List<String> aliases = new ArrayList<>();
    @Setter private Set<String> fileTypes = new HashSet<>();

    public FileHistory(int id, String name, String path) {
        super(id);
        this.path = path;
        this.name = name;
        aliases.add(path);
    }

    public void setNewPath(String newPath) {
        if (!path.equals(newPath)) {
            this.path = newPath;
            this.name = new File(newPath).getName();
            aliases.add(newPath);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileHistory)) return false;
        FileHistory that = (FileHistory) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getPath(), that.getPath()) && Objects.equals(getAliases(), that.getAliases()) && Objects.equals(getFileTypes(), that.getFileTypes()) && fileVersions.size() == that.getFileVersions().size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getPath(), getAliases(), getFileTypes());
    }
}
