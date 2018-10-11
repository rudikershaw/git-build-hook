package com.rudikershaw.gitbuildhook.hook.type;

/** Enumerated types of Git hook. */
public enum GitHookType {

    /** Pre commit hook. */
    PRE_COMMIT("pre-commit"),

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
}
