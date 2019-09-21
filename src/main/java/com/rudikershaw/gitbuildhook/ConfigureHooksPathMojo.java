package com.rudikershaw.gitbuildhook;

import java.io.IOException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.rudikershaw.gitbuildhook.validation.GitRepositoryValidator;

/** Mojo for specifying a custom hooks path in your Git repository config. */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ConfigureHooksPathMojo extends GitRepositoryValidator {

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /** The location the directory to configure to be your new git hooks directory. */
    @Parameter(readonly = true, defaultValue = ".git/hooks/")
    private String hooksPath;

    @Override
    public void execute() throws MojoFailureException {
        // This goal requires the project to have a git repository initialized.
        validateGitRepository(project);

        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());

        try (Git git = Git.open(repoBuilder.getGitDir())) {
            git.getRepository().getConfig().setString("local", null, "core.hooksPath", hooksPath);
            getLog().info("Git hooks directory set to - " + hooksPath);
        } catch (final IOException ioe) {
            failBuildBecauseRepoCouldNotBeFound(ioe);
        }
    }
}
