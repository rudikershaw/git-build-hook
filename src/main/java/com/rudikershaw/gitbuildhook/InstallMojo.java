package com.rudikershaw.gitbuildhook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.rudikershaw.gitbuildhook.hook.type.GitHookType;

/** Mojo for installing Git hooks. */
@Mojo(name = "install", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class InstallMojo extends AbstractMojo {

    /** The location of a pre-commit hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String preCommit;

    /** The location of a pre-push hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String prePush;

    /** The location of a pre-rebase hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String preRebase;

    /** The location of a applypatch-msg hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String applyPatchMsg;

    /** The location of a commit-msg hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String commitMsg;

    /** The location of a prepare-commit-msg hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String prepareCommitMsg;

    /** The location of a pre-applypatch hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String preApplyPatch;

    /** The location of a update hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String update;

    /** The location of a post-update hook script as specified in the plugin configuration. */
    @Parameter(readonly = true)
    private String postUpdate;

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());

        if (repoBuilder.getGitDir() != null) {
            try (Git git = Git.open(repoBuilder.getGitDir())) {
                getLog().info("Found the local git repository.");
                getLog().info("Current branch '" + git.getRepository().getBranch() + "'.");
            } catch (final IOException e) {
                failBuildBecauseRepoCouldNotBeFound(e);
            }
        } else {
            failBuildBecauseRepoCouldNotBeFound(null);
        }

        repoBuilder.findGitDir(project.getBasedir());
        final String hooksDirectory = repoBuilder.getGitDir().toString() + "/hooks";

        installGitHook(GitHookType.PRE_COMMIT, preCommit, hooksDirectory);
        installGitHook(GitHookType.PRE_PUSH, prePush, hooksDirectory);
        installGitHook(GitHookType.PRE_REBASE, preRebase, hooksDirectory);
        installGitHook(GitHookType.PRE_APPLYPATCH, preApplyPatch, hooksDirectory);
        installGitHook(GitHookType.APPLYPATCH_MSG, applyPatchMsg, hooksDirectory);
        installGitHook(GitHookType.COMMIT_MSG, commitMsg, hooksDirectory);
        installGitHook(GitHookType.PREPARE_COMMIT_MSG, prepareCommitMsg, hooksDirectory);
        installGitHook(GitHookType.UPDATE, update, hooksDirectory);
        installGitHook(GitHookType.POST_UPDATE, postUpdate, hooksDirectory);
    }

    /**
     * Take the file in the provided location and install it as a Git hook of the provided type.
     *
     * @param hookType the type of hook to install.
     * @param filePath the location of the file to install as a hook.
     * @param hooksDirectory the directory in which to install the hook.
     */
    private void installGitHook(final GitHookType hookType, final String filePath, final String hooksDirectory) {
        if (filePath != null) {
            try {
                Files.copy(Paths.get(filePath), Paths.get(hooksDirectory + "/" + hookType.getHookFileName()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                getLog().warn("Could not move file into .git/hooks directory", e);
            }
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
