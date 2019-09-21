package com.rudikershaw.gitbuildhook.validation;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/** Abstract class containing the logic required to fail a goal if there is no valid git repository. */
public abstract class GitRepositoryValidator extends AbstractMojo {

    /**
     * Check that a project has a git repository initialized.
     *
     * @param project the Maven project to check.
     * @throws MojoFailureException if no git repository could be found.
     */
    protected void validateGitRepository(final MavenProject project) throws MojoFailureException {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());

        if (repoBuilder.getGitDir() != null) {
            try (Git git = Git.open(repoBuilder.getGitDir())) {
                getLog().info("Found the local git repository.");
            } catch (final IOException e) {
                failBuildBecauseRepoCouldNotBeFound(e);
            }
        } else {
            failBuildBecauseRepoCouldNotBeFound(null);
        }
    }

    /**
     * Throws a MojoFailureException to fail the build.
     * Notifies the user that a git repository could not be found.
     *
     * @param e an exception that caused the build to fail.
     * @throws MojoFailureException to fail the build and with details of the failure.
     */
    protected void failBuildBecauseRepoCouldNotBeFound(final Exception e) throws MojoFailureException {
        final String message = "Could not find or initialise a local git repository. A repository is required.";
        throw new MojoFailureException(message, e);
    }
}
