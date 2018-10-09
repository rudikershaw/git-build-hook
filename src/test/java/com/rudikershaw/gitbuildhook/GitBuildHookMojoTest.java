package com.rudikershaw.gitbuildhook;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.maven.it.Verifier;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitBuildHookMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Test the basic running of the plugin.
     *
     * @throws Exception if anything goes wrong.
     */
    @Test
    public void testDefaultSuccessfulRun() throws Exception {
        final File pom = new File("target/test-classes/default-test-project/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        final GitBuildHookMojo gitBuildHookMojo = (GitBuildHookMojo) rule.lookupConfiguredMojo(pom, "check");
        assertNotNull(gitBuildHookMojo);
        gitBuildHookMojo.execute();
    }

    /**
     * Tests that the plugin fails the build when the project is not managed by Git.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test(expected = MojoFailureException.class)
    public void testFailureFromLackingGitRepo() throws Exception
    {
        Files.copy(Paths.get("target/test-classes/default-test-project/pom.xml"),
                   Paths.get(folder.getRoot().getAbsolutePath() + "/pom.xml"));

        assertTrue(folder.getRoot().exists());
        final GitBuildHookMojo gitBuildHookMojo = (GitBuildHookMojo) rule.lookupConfiguredMojo(folder.getRoot(), "check");
        assertNotNull(gitBuildHookMojo);
        gitBuildHookMojo.execute();
    }

    /**
     * Tests that a new repo is initialised if none exists and the initialise flag is configured.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test
    public void testInitialiseNewGitRepo() throws Exception
    {
        Files.copy(Paths.get("target/test-classes/test-project-with-initialise-true/pom.xml"),
                   Paths.get(folder.getRoot().getAbsolutePath() + "/pom.xml"));

        assertTrue(folder.getRoot().exists());
        final Verifier verifier = new Verifier(folder.getRoot().toString());
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(".git");
        verifier.resetStreams();
    }
}

