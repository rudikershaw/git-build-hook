package com.rudikershaw.gitbuildhook;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertTrue;

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

    @Before
    public void setup() throws IOException {
        FileUtils.copyDirectory(new File("./.mvn"), new File(folder.getRoot().getAbsolutePath() + "/.mvn"));
        Files.copy(Paths.get("mvnw"), Paths.get(folder.getRoot().getAbsolutePath() + "/mvnw"));
        Files.copy(Paths.get("mvnw.cmd"), Paths.get(folder.getRoot().getAbsolutePath() + "/mvnw.cmd"));
    }

    @After
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(folder.getRoot());
    }

    /**
     * Move a file for a specified test folder into a temporary directory for testing.
     *
     * @param testName the name of the test directory in which the files are kept.
     * @param fileName the name of the file to move into the temporary directory.
     * @throws IOException if moving the file in question fails.
     */
    protected void moveToTempTestDirectory(final String testName, final String fileName) throws IOException {
        moveToTempTestDirectory(testName, fileName, fileName);
    }

    /**
     * Move a file for a specified test folder into a temporary directory for testing.
     *
     * @param testName the name of the test directory in which the files are kept.
     * @param fileName the name of the file to move into the temporary directory.
     * @throws IOException if moving the file in question fails.
     */
    protected void moveToTempTestDirectory(final String testName, final String fileName, final String newFileName) throws IOException {
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
