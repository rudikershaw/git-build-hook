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

/** Mojo for intializing a Git repository if one does not already exist. */
@Mojo(name = "initialize", defaultPhase = LifecyclePhase.INITIALIZE)
public class InitialiseMojo extends AbstractMojo {
    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());
        if (repoBuilder.getGitDir() == null) {
            initialiseGitRepository();
        }
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
            throw new MojoFailureException("Could initialise a local git repository.", e);
        }
    }
}
