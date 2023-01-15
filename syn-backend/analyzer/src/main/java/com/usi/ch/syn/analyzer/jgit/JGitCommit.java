package com.usi.ch.syn.analyzer.jgit;

import com.usi.ch.syn.core.git.GitChange;
import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitException;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class JGitCommit extends GitCommit {
    private final Logger logger = LoggerFactory.getLogger(JGitCommit.class);
    private final RevCommit revCommit;
    private final JGitProject jGitProject;


    protected JGitCommit(RevCommit commit, JGitProject jGitProject) {
        super(commit.getCommitTime(), commit.getId().getName(), commit.getShortMessage());
        this.revCommit = commit;
        this.jGitProject = jGitProject;
    }

    @Override
    public List<? extends GitChange> getChangeList() {
        // This code gets all the diffs inside a commit
        List<DiffEntry> diffs;
        try {
            RevWalk revWalk = jGitProject.revWalk;

            if (revWalk.parseCommit(revCommit.toObjectId()).getParentCount() == 0) {
                diffs = jGitProject.getDiffFormatter().scan(null, revCommit.getTree());
            } else {
                RevCommit parent = revWalk.parseCommit(revCommit.getParent(0).getId());
                diffs = jGitProject.getDiffFormatter().scan(parent, revCommit);
            }

            return diffs.parallelStream().map(diffEntry -> new JGitChange(diffEntry, jGitProject)).toList();

        } catch (IOException e) {
            logger.error("Unable to retrieve git diffs of commit {} ", revCommit.getName());
            return List.of();
        }


    }

    @Override
    public void checkout() throws GitException {
        int code = 1;
        try {
            String[] cmd = { "/bin/sh", "-c", "cd " + jGitProject.git.getRepository().getDirectory().getParent() + "; git stash; git clean -f; git checkout " + revCommit.getId().getName() };
            Process p = Runtime.getRuntime().exec(cmd);
            code = p.waitFor();
        } catch (Exception e) {
            code = 1;
            e.printStackTrace();
        }

        if (code == 1) {
            try {
                if (!jGitProject.git.status().call().isClean()) {
                    jGitProject.git.stashCreate().call();
                    jGitProject.git.reset().setMode(ResetCommand.ResetType.HARD).setRef(revCommit.getId().getName()).call();
                }

                jGitProject.git.checkout().setName(revCommit.getId().getName()).call();
            } catch (Exception e) {
                throw new GitException("Unable to checkout to commit " + revCommit.getId().getName());
            }
        }
    }
}
