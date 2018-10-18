package com.rudikershaw.gitbuildhook;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.maven.it.Verifier;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

/** Unit and integration tests for the GitBuildHookMojo. */
public class InstallMojoTest extends AbstractMojoTest {
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

        final InstallMojo installMojo = (InstallMojo) getRule().lookupConfiguredMojo(pom, "install");
        assertNotNull(installMojo);
        installMojo.execute();
    }

    /**
     * Tests that the plugin fails the build when the project is not managed by Git.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test(expected = MojoFailureException.class)
    public void testFailureFromLackingGitRepo() throws Exception {
        moveToTempTestDirectory("default-test-project", "pom.xml");

        final File rootFolder = getFolder().getRoot();
        assertTrue(rootFolder.exists());
        final InstallMojo installMojo = (InstallMojo) getRule().lookupConfiguredMojo(rootFolder, "install");
        assertNotNull(installMojo);
        installMojo.execute();
    }

    /**
     * Tests that a new repo is initialised if none exists and the initialise flag is configured.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test
    public void testInitialiseNewGitRepo() throws Exception {
        moveToTempTestDirectory("test-project-initialise", "pom.xml");

        final File rootFolder = getFolder().getRoot();
        assertTrue(rootFolder.exists());
        final Verifier verifier = new Verifier(rootFolder.toString());
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(".git");
        verifier.resetStreams();
    }

    /**
     * Tests that a new repo is initialised if none exists and the initialise flag is configured.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test
    public void testInstallTwoHooks() throws Exception {
        moveToTempTestDirectory("test-project-install-hooks", "pom.xml");
        moveToTempTestDirectory("test-project-install-hooks", "hook-to-install.sh");

        final File rootFolder = getFolder().getRoot();
        assertTrue(rootFolder.exists());
        final Verifier verifier = new Verifier(rootFolder.toString());
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(".git/hooks/pre-commit");
        verifier.assertFilePresent(".git/hooks/pre-push");
        verifier.assertFilePresent(".git/hooks/pre-rebase");
        verifier.assertFilePresent(".git/hooks/commit-msg");
        verifier.assertFilePresent(".git/hooks/prepare-commit-msg");
        verifier.assertFilePresent(".git/hooks/update");
        verifier.assertFilePresent(".git/hooks/post-update");
        verifier.assertFilePresent(".git/hooks/applypatch-msg");
        verifier.assertFilePresent(".git/hooks/pre-applypatch");
        verifier.resetStreams();
    }
}

