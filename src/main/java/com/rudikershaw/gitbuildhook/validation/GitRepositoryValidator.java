package com.rudikershaw.gitbuildhook.validation;

import java.io.File;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/** Interface containing the logic required to fail a goal if there is no valid git repository. */
public interface GitRepositoryValidator {

    /**
     * Check that a project has a git repository initialized.
     *
     * @param project the Maven project to check.
     * @throws MojoFailureException if no git repository could be found.
     */
    default void validateGitRepository(final MavenProject project) throws MojoFailureException {
        if (!isGitRepoInitialised(project)) {
            failBuildBecauseRepoCouldNotBeFound(null);
        }
        createGitHooksDirectory(project);
    }

    /**
     * Create .git/hooks directory if one does not already exist.
     *
     * @param project the Maven project to check.
     * @throws MojoFailureException if the hooks directory could not be created.
     */
    default void createGitHooksDirectory(final MavenProject project) throws MojoFailureException {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());
        final String hooksDirectory = repoBuilder.getGitDir().toString()
            + File.separator
            + "hooks";
        final File hooksDirFile = new File(hooksDirectory);
        if (!hooksDirFile.exists() && !hooksDirFile.mkdirs()) {
            throw new MojoFailureException("Could not create .git/hooks directory.");
        }
    }

    /**
     * Returns true if there is already a valid git repository, otherwise false.
     *
     * @param project the Maven project to check.
     * @return whether a git repository is initialized.
     */
    default boolean isGitRepoInitialised(MavenProject project) {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());
        return repoBuilder.getGitDir() != null;
    }

    /**
     * Throws a MojoFailureException to fail the build.
     * Notifies the user that a git repository could not be found.
     *
     * @param e an exception that caused the build to fail.
     * @throws MojoFailureException to fail the build and with details of the failure.
     */
    default void failBuildBecauseRepoCouldNotBeFound(final Exception e) throws MojoFailureException {
        final String message = "Could not find or initialise a local git repository. A repository is required.";
        throw new MojoFailureException(message, e);
    }
}
