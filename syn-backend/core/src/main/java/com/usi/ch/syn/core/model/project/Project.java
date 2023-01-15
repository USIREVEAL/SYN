package com.usi.ch.syn.core.model.project;

import com.usi.ch.syn.core.model.CodeEntity;
import com.usi.ch.syn.core.model.history.ProjectHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public abstract class Project extends CodeEntity {

    private final String name;

    @Setter
    private String path;

    private transient ProjectHistory projectHistory;

    protected Project(int id, String name, String path) {
        super(id);
        this.name = name;
        this.path = path;
    }

    public void setProjectHistory(final ProjectHistory projectHistory) {
        assert projectHistory.getProject().equals(this);
        this.projectHistory = projectHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        if (!super.equals(o)) return false;
        Project project = (Project) o;
        return getName().equals(project.getName()) && getPath().equals(project.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getPath());
    }
}
