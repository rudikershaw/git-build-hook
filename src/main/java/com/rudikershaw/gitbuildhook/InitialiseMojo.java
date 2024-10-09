package com.rudikershaw.gitbuildhook;

import com.rudikershaw.gitbuildhook.threadsafety.ClassLock;
import com.rudikershaw.gitbuildhook.validation.GitRepositoryValidator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/** Mojo for initializing a Git repository if one does not already exist. */
@Mojo(name = "initialize", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class InitialiseMojo extends AbstractMojo implements GitRepositoryValidator {

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /** Skip initialising the git repo. */
    @Parameter(property = "gitbuildhook.init.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoFailureException {
        if (skip) {
            getLog().debug("Skipping");
            return;
        }

        if (!isGitRepoInitialised(project)) {
            initialiseGitRepository();
        } else {
            getLog().info("A Git repository is already initialized.");
        }
    }

    /**
     * Initialise a new git repository in the Maven project base directory.
     *
     * @throws MojoFailureException to fail the build and with details of the failure.
     */
    private void initialiseGitRepository() throws MojoFailureException {
        try {
            synchronized (ClassLock.class) {
                Git.init().setDirectory(project.getBasedir()).call();
            }
        } catch (final GitAPIException e) {
            if (!isGitRepoInitialised(project)) {
                throw new MojoFailureException("Could not initialise a local git repository.", e);
            } else {
                getLog().warn("Tried to initialize a Git repository, but a repository already exists.");
            }
        }
    }
}
