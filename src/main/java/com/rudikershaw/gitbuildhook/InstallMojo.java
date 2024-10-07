package com.rudikershaw.gitbuildhook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.rudikershaw.gitbuildhook.hook.type.GitHookType;
import com.rudikershaw.gitbuildhook.validation.GitRepositoryValidator;

/** Mojo for installing Git hooks. */
@Mojo(name = "install", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class InstallMojo extends AbstractMojo implements GitRepositoryValidator {

    /** The location of git hooks to install into the default hooks directory. */
    @Parameter
    private final Map<String, String> installHooks = new HashMap<>();

    /** Injected MavenProject containing project related information such as base directory. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /** Skip installing hooks. */
    @Parameter(property = "gitbuildhook.install.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoFailureException {
        if (skip) {
            getLog().debug("Skipping");
            return;
        }

        // This goal requires the project to have a git repository initialized.
        validateGitRepository(project);

        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(project.getBasedir());
        final String hooksDirectory = repoBuilder.getGitDir().toString() + "/hooks";

        for (final Map.Entry<String, String> hook : installHooks.entrySet()) {
            final String hookName = hook.getKey();
            if (GitHookType.isValidHookName(hookName)) {
                installGitHook(hookName, hook.getValue(), hooksDirectory);
            } else {
                throw new MojoFailureException("'" + hookName + "' is not a valid hook file name.");
            }
        }
    }

    /**
     * Take the file in the provided location and install it as a Git hook of the provided type.
     *
     * @param hookName the type of hook to install.
     * @param filePath the location of the file to install as a hook.
     * @param hooksDirectory the directory in which to install the hook.
     */
    private void installGitHook(final String hookName, final String filePath, final String hooksDirectory) {
        if (Objects.nonNull(filePath)) {
            final String gitHookPathStr = hooksDirectory + File.separator + hookName;
            if (Paths.get(filePath).toFile().isFile()) {
                copyFromFile(filePath, gitHookPathStr);
            } else {
                copyFromClasspath(filePath, gitHookPathStr);
            }
        }
    }

    /**
     * Copies the specified file from the file system into the default hooks directory.
     *
     * @param filePath path to the file to use as the hook.
     * @param gitHookPathStr the location to move the file to.
     */
    private void copyFromFile(final String filePath, final String gitHookPathStr) {
        try {
            Files.copy(Paths.get(filePath), Paths.get(gitHookPathStr), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            getLog().warn("Could not move file into .git/hooks directory", e);
        }
    }

    /**
     * Copies the specified file from the classpath into the the default hooks directory.
     *
     * @param filePath path to the file to use as the hook.
     * @param gitHookPathStr the location to move the file to.
     */
    private void copyFromClasspath(final String filePath, final String gitHookPathStr) {
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        if (Objects.isNull(resource)) {
            getLog().warn("Could not find file on filesystem or classpath");
        } else {
            try {
                final File gitHookFile = Paths.get(gitHookPathStr).toFile();
                IOUtils.copy(resource.openStream(), new FileOutputStream(gitHookFile));
                gitHookFile.setExecutable(true);
            } catch (final IOException e) {
                getLog().warn("Could not move file from classpath into .git/hooks directory", e);
            }
        }
    }
}
