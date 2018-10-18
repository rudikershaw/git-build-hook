package com.rudikershaw.gitbuildhook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/** Abstract test for Mojos. */
public class AbstractMojoTest {
    private MojoRule rule = new MojoRule();

    private TemporaryFolder folder = new TemporaryFolder();

    /**
     * Move a file for a specified test folder into a temporary directory for testing.
     *
     * @param testName the name of the test directory in which the files are kept.
     * @param fileName the name of the file to move into the temporary directory.
     * @throws IOException if moving the file in question fails.
     */
    protected void moveToTempTestDirectory(final String testName, final String fileName) throws IOException {
        Files.copy(Paths.get("target/test-classes/" + testName + "/" + fileName),
                   Paths.get(folder.getRoot().getAbsolutePath() + "/" + fileName));
    }

    /**
     * Returns the rule.
     *
     * @return the rule.
     */
    @Rule
    public MojoRule getRule() {
        return rule;
    }

    /**
     * Returns the folder.
     *
     * @return the folder.
     */
    @Rule
    public TemporaryFolder getFolder() {
        return folder;
    }
}
