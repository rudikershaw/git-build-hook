package com.rudikershaw.gitbuildhook;

import java.io.IOException;
import java.util.Map;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.rudikershaw.gitbuildhook.validation.GitRepositoryValidator;

/** Mojo for specifying a custom git config settings for your Git repository. */
@Mojo(name = "configure", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class GitConfigMojo extends GitRepositoryValidator {

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /** The git config to set and the values to set them to. */
    @Parameter
    private Map<String, String> gitConfig;

    @Override
    public void execute() throws MojoFailureException {
        // This goal requires the project to have a git repository initialized.
        validateGitRepository(project);

        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());

        try (Git git = Git.open(repoBuilder.getGitDir())) {
            final StoredConfig config = git.getRepository().getConfig();
            for (final Map.Entry<String, String> entry : gitConfig.entrySet()) {
                final String[] conf = stringToConfigArray(entry.getKey());
                config.setString(conf[0], conf[1], conf[2], entry.getValue());
                getLog().info("Git config '" + entry.getKey() + "' set to - " + entry.getValue());
            }
            config.save();
        } catch (final IOException ioe) {
            failBuildBecauseRepoCouldNotBeFound(ioe);
        }
    }

    /**
     * Takes a git config key string (e.g. core.hooksPath) and splits it into section, subsection, and name.
     * The former two are set to null is missing from the provided string.
     *
     * @param string a git config key string.
     * @return an array representing git config key section, subsection, and name respectively.
     *
     * @throws MojoFailureException if the git config key string is invalid.
     */
    private String[] stringToConfigArray(final String string) throws MojoFailureException {
        final String[] split = string.split("\\.");
        final byte sections = 3;
        if (split.length > sections || split.length < 2) {
            throw new MojoFailureException("Git config '" + string + "' must include 1-2 sections separated by stops.");
        }

        final String name = split[split.length - 1];
        final String subsection = split.length == sections ? split[1] : null;
        final String section = split[0];

        return new String[]{section, subsection, name};
    }
}
