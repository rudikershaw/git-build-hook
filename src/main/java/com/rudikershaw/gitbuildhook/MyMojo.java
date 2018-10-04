package com.rudikershaw.gitbuildhook;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/** Starting location for the Git Build Hook plugin and home of the execute method. */
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MyMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoFailureException {
        getLog().info("#######################################################");
        getLog().info("#######################################################");
        getLog().info("#######################################################");
    }
}
