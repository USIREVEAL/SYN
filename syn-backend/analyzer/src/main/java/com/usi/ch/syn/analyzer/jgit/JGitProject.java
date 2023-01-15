package com.usi.ch.syn.analyzer.jgit;

import com.usi.ch.syn.core.git.GitCommit;
import com.usi.ch.syn.core.git.GitException;
import com.usi.ch.syn.core.git.GitProject;
import com.usi.ch.syn.core.model.analysis.AnalysisWorkDescriptor;
import com.usi.ch.syn.core.model.project.LocalProject;
import com.usi.ch.syn.core.model.project.Project;
import com.usi.ch.syn.core.model.project.RemoteProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.usi.ch.syn.core.utils.Config.getGitProjectFolderPath;

//https://github.com/centic9/jgit-cookbook

public class JGitProject extends GitProject {
    static Logger logger = LoggerFactory.getLogger(JGitProject.class);
    protected Git git;
    protected Repository repository;
    protected RevWalk revWalk;

    public JGitProject(final Project project) {
        super(project);

        if (!Files.exists(Path.of(project.getPath()))) {
            project.setPath(getGitProjectFolderPath(project.getName(), project.getId()).toString());
        }

        if (project instanceof RemoteProject remoteProject) {
            initializeRemoteProject(remoteProject);
        } else if (project instanceof LocalProject localProject) {
            initializeLocalProject(localProject);
        }
    }

    @Override
    public void closeResource() {
        git.close();
    }


    public List<GitCommit> getCommits(String path) {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            Iterable<RevCommit> logs = git.log().addPath(path).call();
            List<GitCommit> commitList = new ArrayList<>();
            for (RevCommit rev : logs) {
                commitList.add(new JGitCommit(rev, this));
            }
            return commitList;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


    private void initializeRemoteProject(RemoteProject project) {
        try {
            File gitProjectDirectory = new File(project.getPath());

            if (gitProjectDirectory.exists()) {
                System.out.println("Found an already existing repository on " + gitProjectDirectory.getAbsolutePath());
                File gitFolderPath = new File(gitProjectDirectory + "/.git");
                git = Git.open(gitFolderPath);
                this.repository = git.getRepository();
                this.revWalk = new RevWalk(repository);
            } else {
                if (gitProjectDirectory.mkdir()) {
                    System.out.println("Cloning repository " + project.getProjectURL() + "  to " + gitProjectDirectory.getAbsolutePath());
                    long entryTime = System.currentTimeMillis();

                    git = Git.cloneRepository()
                            .setURI(project.getProjectURL())
                            .setDirectory(gitProjectDirectory)
//                            .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                            .call();

                    logger.info("Repository cloning completed (took {}s)", ((System.currentTimeMillis() - entryTime) / 1000));
                } else {
                    logger.error("Unable to create project folder at {} ", gitProjectDirectory.getAbsolutePath());
                }
            }

            this.repository = git.getRepository();
            this.revWalk = new RevWalk(repository);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void initializeLocalProject(LocalProject project) {
        try {
            File gitProjectDirectory = new File(project.getPath());
            logger.info("Creating new project on {}", gitProjectDirectory.getAbsolutePath());
            File gitFolderPath = new File(project.getPath() + "/.git");

            if (gitFolderPath.exists()) {
                git = Git.open(gitFolderPath);
                this.repository = git.getRepository();
                this.revWalk = new RevWalk(repository);
            } else {
                throw new RuntimeException("Unable to find the git folder at" + gitFolderPath.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pullAndRestoreRepo() {
        try {
            git.pull().call();
            git.checkout().setForced(true).setName(getMasterBranchName()).call();

        } catch (Exception e) {
            logger.warn("Unable to pull and checkout the master branch");
        }
    }

    @Override
    public List<GitCommit> getCommits(final String commitFrom, final String commitTo) {
        List<GitCommit> commitList = new ArrayList<>();
        try {
            String checkoutBranchName = commitTo;
            if (commitTo == null || commitTo.equals("")) {
                checkoutBranchName = getMasterBranchName();
            }

            logger.info("Calling checkout command to branch {}", checkoutBranchName);
            git.checkout().setName(checkoutBranchName).call();

            Iterator<RevCommit> commitIterator = git.log().call().iterator();
            RevCommit revCommit;
            boolean foundCommitTo = false;
            while (commitIterator.hasNext() && !(foundCommitTo)) {
                revCommit = commitIterator.next();
                foundCommitTo = revCommit.getId().getName().equals(commitFrom);
                if (revCommit.getParentCount() < 2)
                    commitList.add(new JGitCommit(revCommit, this));
            }

            Collections.reverse(commitList);
        } catch (GitAPIException e) {
            logger.error("Unable to inspect GIT");
        }

        return commitList;
    }

    @Override
    public List<GitCommit> getCommits(final AnalysisWorkDescriptor analysisWorkDescriptor) throws GitException {
        List<String> commitHashList = analysisWorkDescriptor.commits();
        int commitHashListIndex = commitHashList.size() - 1;
        List<GitCommit> commitList = new ArrayList<>();

        try {
            String checkoutBranchName = commitHashList.get(commitHashListIndex);
            String lastCommitHash = commitHashList.get(0);
            logger.info("Calling checkout command to branch {}", checkoutBranchName);
            try {
                smartCheckout(checkoutBranchName);
            } catch (JGitInternalException e) {
                throw new GitException(e.getMessage());
            }

            Iterator<RevCommit> commitIterator = git.log().setRevFilter(RevFilter.NO_MERGES).call().iterator();
            RevCommit revCommit;
            boolean foundCommitTo = false;
            while (commitIterator.hasNext() && !(foundCommitTo) && commitHashListIndex >= 0) {
                revCommit = commitIterator.next();

                if (revCommit.getId().getName().equals(commitHashList.get(commitHashListIndex))) {
                    commitHashListIndex--;
                } else {
                    checkoutBranchName = commitHashList.get(commitHashListIndex);
                    smartCheckout(checkoutBranchName);
                    commitIterator = git.log().call().iterator();
                    continue;
                }

                foundCommitTo = revCommit.getId().getName().equals(lastCommitHash);
                if (revCommit.getParentCount() < 2)
                    commitList.add(new JGitCommit(revCommit, this));
            }

            Collections.reverse(commitList);
        } catch (GitAPIException e) {
            logger.error("Unable to inspect GIT");
        }

        return commitList;
    }

    private void smartCheckout(String checkoutBranchName) throws GitAPIException {
        int code = 1;
        try {
            String[] cmd = { "/bin/sh", "-c", "cd " + git.getRepository().getDirectory().getParent() + "; git stash; git clean -f; git checkout " + checkoutBranchName };
            Process p = Runtime.getRuntime().exec(cmd);
            code = p.waitFor();
        } catch (Exception e) {
            code = 1;
            e.printStackTrace();
        }

        if (code == 1) {
            git.checkout().setName(checkoutBranchName).call();
        }
    }
//    @Override
//    public List<GitCommit> getCommits() {
//        List<GitCommit> commitList = new ArrayList<>();
//        try {
//            String branchName = getBranchName();
//            git.checkout().setName(branchName).call();
//            logger.info("Analyzing branch {}", repository.getBranch());
//            git.log().call().forEach(revCommit -> {
//                if (revCommit.getParentCount() < 2)
//                    commitList.add(new JGitCommit(revCommit, this));
//            });
//
//            Collections.reverse(commitList);
//        } catch (GitAPIException e) {
//            logger.error("Unable to inspect GIT");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return commitList;
//    }

    private Git getGitFromProject(Project project) {
        try {
            return Git.open(new File(project.getPath() + "/.git"));
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate git for project " + project.getName());
        }
    }


    private String getMasterBranchName() throws GitAPIException {
        List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        List<String> branchNames = branches.stream().map(ref -> {
            String[] refSplit = ref.getName().split("/");
            return refSplit[refSplit.length - 1];
        }).toList();

        String branchName = branchNames.get(0);
        if (branchNames.contains("master")) {
            branchName = "master";
        } else if (branchNames.contains("main")) {
            branchName = "main";
        }
        return branchName;
    }

    protected DiffFormatter getDiffFormatter() {
        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);

        diffFormatter.setRepository(repository);
        diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
        diffFormatter.setDetectRenames(true);

        return diffFormatter;
    }

}
