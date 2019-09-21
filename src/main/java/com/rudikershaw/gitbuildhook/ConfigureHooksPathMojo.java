package com.rudikershaw.gitbuildhook;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.rudikershaw.gitbuildhook.validation.GitRepositoryValidator;

/** Mojo for specifying a custom hooks path in your Git repository config. */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ConfigureHooksPathMojo extends GitRepositoryValidator {

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        // This goal requires the project to have a git repository initialized.
        validateGitRepository(project);
    }
}
