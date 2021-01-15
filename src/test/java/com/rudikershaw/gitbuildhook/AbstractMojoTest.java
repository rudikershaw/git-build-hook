package com.rudikershaw.gitbuildhook;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.maven.it.VerificationException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.apache.maven.it.Verifier;

/** Abstract test for Mojos. */
public class AbstractMojoTest {

    private final MojoRule rule = new MojoRule();

    private final TemporaryFolder folder = new TemporaryFolder();

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

    /**
     * Move a file for a specified test folder into a temporary directory for testing.
     *
     * @param testName the name of the test directory in which the files are kept.
     * @param fileName the name of the file to move into the temporary directory.
     * @param folder a temporary folder to use.
     * @throws IOException if moving the file in question fails.
     */
    protected void moveToTempTestDirectory(final String testName, final String fileName, final TemporaryFolder folder) throws IOException {
        moveToTempTestDirectory(testName, fileName, fileName, folder);
    }

    /**
     * Move a file for a specified test folder into a temporary directory for testing.
     *
     * @param testName the name of the test directory in which the files are kept.
     * @param fileName the name of the file to move into the temporary directory.
     * @param folder a temporary folder to use.
     * @throws IOException if moving the file in question fails.
     */
    protected void moveToTempTestDirectory(final String testName, final String fileName, final String newFileName, final TemporaryFolder folder) throws IOException {
        Files.copy(Paths.get("target/test-classes/" + testName + "/" + fileName),
                   Paths.get(folder.getRoot().getAbsolutePath() + "/" + newFileName),
                   StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }

    /**
     * Gets a verifier configured to pull dependencies to a test repo that the project is installed to before tests.
     *
     * @param project the directory containing the pom.xml for testing.
     * @return a verifier.
     * @throws VerificationException
     */
    protected Verifier getVerifier(final String project) throws VerificationException {
        final Verifier verifier = new Verifier(project);
        final File testRepsotiroyDirectory = new File("target/test-repo");
        assertTrue("Plugin must be installed into a local repo for tests", testRepsotiroyDirectory.exists());
        verifier.setLocalRepo(testRepsotiroyDirectory.getAbsolutePath());
        return verifier;
    }

}
