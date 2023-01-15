package com.usi.ch.syn.analyzer.jgit;

import com.usi.ch.syn.core.git.GitChange;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGitChange extends GitChange {
    private final Logger logger = LoggerFactory.getLogger(JGitChange.class);
    private final EditListValues editListValues;
    private final DiffEntry diffEntry;
    private final JGitProject jGitProject;

    protected JGitChange(DiffEntry diffEntry, JGitProject jGitProject) {
       this.diffEntry = diffEntry;
       this.jGitProject = jGitProject;
       this.editListValues = getEditListValues();
    }

    @Override
    public ChangeType getChangeType() {
        return switch (diffEntry.getChangeType()) {
            case MODIFY -> ChangeType.MODIFY;
            case RENAME -> ChangeType.RENAME;
            case ADD, COPY -> ChangeType.ADD;
            case DELETE -> ChangeType.DELETE;
        };
    }

    @Override
    public String getOldPath() {
        return diffEntry.getOldPath();
    }

    @Override
    public String getNewPath() {
        return diffEntry.getNewPath();
    }

    @Override
    public int getLinesAdd() {
        return editListValues.linesAdded;
    }

    @Override
    public int getLinesDel() {
        return  editListValues.linesDeleted;
    }

    private record EditListValues(int linesAdded, int linesDeleted) { }

    private EditListValues getEditListValues() {
        if (editListValues != null)
            return editListValues;

        try {
            int linesAdded = 0;
            int linesDeleted = 0;
            for (Edit edit : jGitProject.getDiffFormatter().toFileHeader(diffEntry).toEditList()) {
                linesDeleted += edit.getEndA() - edit.getBeginA();
                linesAdded += edit.getEndB() - edit.getBeginB();
            }

            return new EditListValues(linesAdded, linesDeleted);
        } catch (Exception e) {
            logger.warn("Unable to process the EditList of " + this);
            return new EditListValues(0, 0);
        }
    }
}
