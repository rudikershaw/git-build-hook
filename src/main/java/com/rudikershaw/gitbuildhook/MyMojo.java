package com.rudikershaw.gitbuildhook;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jgit.api.Git;

/** Starting location for the Git Build Hook plugin and home of the execute method. */
@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MyMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoFailureException {
        try (final Git git = Git.open(Paths.get(".").toFile())) {
            getLog().info("Found the local git repo.");
        } catch (IOException e) {
            getLog().error("Could not find the local git repository. A git repository is required for this build.", e);
        }
    }
}
