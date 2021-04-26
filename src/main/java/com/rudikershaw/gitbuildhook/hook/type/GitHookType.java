package com.rudikershaw.gitbuildhook.hook.type;

import java.util.Arrays;

/** Enumerated types of Git hook. */
public enum GitHookType {

    /** Pre commit hook. */
    PRE_COMMIT("pre-commit"),

    /** Pre merge commit hook. */
    PRE_MERGE_COMMIT("pre-merge-commit"),

    /** Pre push hook. */
    PRE_PUSH("pre-push"),

    /** Pre rebase hook. */
    PRE_REBASE("pre-rebase"),

    /** Commit message hook. */
    COMMIT_MSG("commit-msg"),

    /** Prepare commit message. */
    PREPARE_COMMIT_MSG("prepare-commit-msg"),

    /** The update hook. */
    UPDATE("update"),

    /** The post update hook. */
    POST_UPDATE("post-update"),

    /** The apply patch message hook. */
    APPLYPATCH_MSG("applypatch-msg"),

    /** The pre apply patch hook. */
    PRE_APPLYPATCH("pre-applypatch");

    /** The pre-specified name of the file of this hook type. */
    private final String hookFileName;

    /**
     * Constructor.
     *
     * @param fileName the name of the file for the hook of this type.
     */
    GitHookType(final String fileName) {
        hookFileName = fileName;
    }

    /**
     * Gets the pre-specified name of the file for hooks of this type.
     *
     * @return the hook file name.
     */
    public String getHookFileName() {
        return hookFileName;
    }

    /**
     * Takes a string filename and returns true if, and only if, the string is a valid hook name.
     *
     * @param filename the filename to check.
     * @return true if the provided string is a valid hook name, otherwise false.
     */
    public static boolean isValidHookName(final String filename) {
        return Arrays.stream(values()).anyMatch(h -> h.getHookFileName().equals(filename));
    }
}
