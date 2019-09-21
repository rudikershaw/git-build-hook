package com.rudikershaw.gitbuildhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

/** Unit and integration tests for the GitBuildHookMojo. */
public class ConfigureHooksPathMojoTest extends AbstractMojoTest {

    /**
     * Tests that the hook, installed by changing the hooks directory, prevents a commit.
     *
     * @throws Exception if a temp project cannot be created for testing.
     */
    @Test
    public void testConfigureGitHooksDirectory() throws Exception {
        moveToTempTestDirectory("test-project-configure", "pom.xml");
        final File rootFolder = getFolder().getRoot();
        assertTrue(rootFolder.exists());

        final Verifier verifier = getVerifier(rootFolder.toString());
        verifier.executeGoal("install");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(".git");
        verifier.resetStreams();

        final FileRepositoryBuilder repoBuilder =  new FileRepositoryBuilder();
        repoBuilder.findGitDir(rootFolder);

        try (Git git = Git.open(repoBuilder.getGitDir())) {
            assertEquals("hooks-path/", git.getRepository().getConfig().getString("core", null, "hooksPath"));
        }
    }
}

