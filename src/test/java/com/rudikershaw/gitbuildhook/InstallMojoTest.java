package com.rudikershaw.gitbuildhook;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.it.Verifier;
import org.apache.maven.plugin.MojoFailureException;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.rules.TemporaryFolder;

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
    @Test
    public void testFailureFromLackingGitRepo() throws Exception {
        final TemporaryFolder folder = getFolder();
        moveToTempTestDirectory("default-test-project", "pom.xml", folder);

        final File rootFolder = folder.getRoot();
        assertTrue(rootFolder.exists());
        final InstallMojo installMojo = (InstallMojo) getRule().lookupConfiguredMojo(rootFolder, "install");
        assertNotNull(installMojo);

        final Executable testMethod = () -> installMojo.execute();
        final MojoFailureException thrown = assertThrows(MojoFailureException.class, testMethod);
        assertThat(thrown.getMessage(), Is.is(IsEqual.equalTo("Could not find or initialise a local git repository. A repository is required.")));
    }

    /**
     * Tests that a new repo is initialised if none exists and the initialise flag is configured.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test
    public void testInstallTwoHooks() throws Exception {
        final TemporaryFolder folder = getFolder();
        moveToTempTestDirectory("test-project-install-hooks", "pom.xml", folder);
        moveToTempTestDirectory("test-project-install-hooks", "hook-to-install.sh", folder);

        final File rootFolder = folder.getRoot();
        assertTrue(rootFolder.exists());
        final Verifier verifier = getVerifier(rootFolder.toString());
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

    /**
     * Test that you can update hooks by installing one file over another.
     *
     * @throws Exception if a temp project cannot be created for testing.
     */
    @Test
    public void testUpdateHooks() throws Exception {
        final TemporaryFolder folder = getFolder();
        moveToTempTestDirectory("test-project-reinstall-hooks", "pom.xml", folder);
        moveToTempTestDirectory("test-project-reinstall-hooks", "hook-to-install.sh", folder);
        moveToTempTestDirectory("test-project-reinstall-hooks", "hook-to-reinstall.sh", folder);

        final File rootFolder = folder.getRoot();
        Verifier verifier = getVerifier(rootFolder.toString());
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(".git/hooks/pre-commit");
        verifier.assertFilePresent(".git/hooks/commit-msg");

        List<String> lines = verifier.loadFile(new File(rootFolder, ".git/hooks/pre-commit"), false);
        assertTrue(lines.contains("install"));
        List<String> origionalCommitMsgLines = verifier.loadFile(new File(rootFolder, ".git/hooks/commit-msg"), false);
        assertTrue(origionalCommitMsgLines.contains("origional hook"));

        moveToTempTestDirectory("test-project-reinstall-hooks", "pom2.xml", "pom.xml", folder);
        verifier = getVerifier(rootFolder.toString());
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(".git/hooks/pre-commit");
        verifier.assertFilePresent(".git/hooks/commit-msg");

        lines = verifier.loadFile(new File(rootFolder, ".git/hooks/pre-commit"), false);
        assertTrue(lines.contains("reinstall"));
        List<String> updatedCommitMsgLines = verifier.loadFile(new File(rootFolder, ".git/hooks/commit-msg"), false);
        assertTrue(updatedCommitMsgLines.contains("updated hook"));
    }

    /**
     * Tests that the plugin fails when we specify installing an invalid named hook.
     *
     * @throws IOException if a temp project cannot be created for testing.
     */
    @Test
    public void testFailureFromInvalidHookNames() throws Exception {
        final TemporaryFolder folder = getFolder();
        moveToTempTestDirectory("test-project-invalid-hook", "pom.xml", folder);

        final File rootFolder = folder.getRoot();
        assertTrue(rootFolder.exists());
        final InstallMojo installMojo = (InstallMojo) getRule().lookupConfiguredMojo(rootFolder, "install");
        assertNotNull(installMojo);

        final Executable testMethod = () -> installMojo.execute();
        final MojoFailureException thrown = assertThrows(MojoFailureException.class, testMethod);
        assertThat(thrown.getMessage(), Is.is(IsEqual.equalTo("Could not find or initialise a local git repository. A repository is required.")));
    }

}
