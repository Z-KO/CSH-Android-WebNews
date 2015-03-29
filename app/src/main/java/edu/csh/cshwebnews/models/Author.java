package edu.csh.cshwebnews.models;

/**
 * Represents the author of a post.
 */
public class Author {

    private String name;

    private String email;

    private String raw;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @return Raw content of "From" header.
     */
    public String getRaw() {
        return raw;
    }
}
