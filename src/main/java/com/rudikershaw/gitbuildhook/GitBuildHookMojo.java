package com.rudikershaw.gitbuildhook;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/** Starting location for the Git Build Hook plugin and home of the execute method. */
@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GitBuildHookMojo extends AbstractMojo {

    /** Whether to initialise a git repository if one does not already exist. */
    @Parameter(readonly = true)
    private boolean initialise;

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder().findGitDir(project.getBasedir());
        if (repoBuilder.getGitDir() != null) {
            try (Git git = Git.open(repoBuilder.getGitDir())) {
                getLog().info("Found the local git repository.");
                getLog().info("Current branch '" + git.getRepository().getBranch() + "'.");
            } catch (final IOException e) {
                failBuildBecauseRepoCouldNotBeFound(e);
            }
        } else if (initialise) {
            initialiseGitRepository();
        } else {
            failBuildBecauseRepoCouldNotBeFound(null);
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
            failBuildBecauseRepoCouldNotBeFound(e);
        }
    }

    /**
     * Throws a MojoFailureException to fail the build.
     * Notifies the user that a git repository could not be found.
     *
     * @param e an exception that caused the build to fail.
     * @throws MojoFailureException to fail the build and with details of the failure.
     */
    private void failBuildBecauseRepoCouldNotBeFound(final Exception e) throws MojoFailureException {
        final String message = "Could not find or initialise a local git repository. A repository is required.";
        throw new MojoFailureException(message, e);
    }
}
