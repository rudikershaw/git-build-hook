package com.rudikershaw.gitbuildhook;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/** Mojo for initializing a Git repository if one does not already exist. */
@Mojo(name = "initialize", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class InitialiseMojo extends AbstractMojo {

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        if (!isGitRepoInitialised()) {
            initialiseGitRepository();
        } else {
            getLog().info("A Git repository is already initialized.");
        }
    }

    /**
     * Returns true if there is already a valid git repository, otherwise false.
     *
     * @return whether a git repository is initialized.
     */
    private boolean isGitRepoInitialised() {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());
        return repoBuilder.getGitDir() != null;
    }
    /**
     * Initialise a new git repository in the Maven project base directory.
     *
     * @throws MojoFailureException to fail the build and with details of the failure.
     */
    private void initialiseGitRepository() throws MojoFailureException {
        try {
            Git.init().setDirectory(project.getBasedir()).call();
        } catch (final GitAPIException e) {
            if (!isGitRepoInitialised()) {
                throw new MojoFailureException("Could not initialise a local git repository.", e);
            } else {
                getLog().warn("Tried to initialize a Git repository, but a repository already exists.");
            }
        }
    }
}
