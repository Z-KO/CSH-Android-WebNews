/*
 * Represents an individual post
 * https://github.com/grantovich/CSH-WebNews/wiki/API%3A-Retrieving-Posts
 * TODO: Write method to check if post is stickied
 *
 * Peter Zujko
 */

package edu.csh.cshwebnews.models;

import java.util.List;

public class Post {

    private Integer id;

    private List<Integer> ancestor_ids;

    private Author author;

    private String body;

    private String created_at;

    private Integer followup_newsgroup_id;

    private Boolean had_attachments;

    private String headers;

    private Boolean is_dethreaded;

    private Boolean is_starred;

    private String message_id;

    private List<Integer> newsgroup_ids;

    private Integer personal_level;

    private Sticky sticky;

    private String subject;

    private Integer total_stars;

    private String unread_class;

    /**
     * @return The unique ID of the post on WebNews.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return List of post IDs tracing the reply chain that leads to this post.
     * The first element is the original post in the thread ("root"), and the last is the post
     * that this post is a direct reply to. The array may be empty,
     * indicating this post is the "root" of its thread.
     */
    public List<Integer> getListOfAncestorIds() {
        return ancestor_ids;
    }

    /**
     * @return An Author object
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * @return The plaintext body of the post.
     */
    public String getBody() {
        return body;
    }

    /**
     * @return When the post was originally made.
     */
    public String getCreatedAt() {
        return created_at;
    }

    /**
     * @return The ID of the newsgroup that replies to this post should go in,
     * or null if the author did not specify one. Usually only set for multi-newsgroup posts.
     */
    public Integer getFollowupNewsgroupId() {
        return followup_newsgroup_id;
    }

    /**
     * @return If true, this post had one or more attached files
     * that were stripped on import into WebNews.
     */
    public Boolean hadAttachments() {
        return had_attachments;
    }

    /**
     * @return The raw headers of the post.
     */
    public String getHeaders() {
        return headers;
    }

    /**
     * @return If true, the post that this post is supposedly a reply to (according to its headers)
     * could not be located, and its ancestor_ids are therefore an educated guess.
     */
    public Boolean isDethreaded() {
        return is_dethreaded;
    }

    /**
     * @return If true, this post is starred by the current user.
     */
    public Boolean isStarred() {
        return is_starred;
    }

    /**
     * @return The unique ID of the post on the news server. WebNews does not store the angle brackets,
     * so this will look like foo@bar.baz rather than <foo@bar.baz>.
     */
    public String getMessageId() {
        return message_id;
    }

    /**
     * @return List of newsgroup IDs indicating which newsgroups this post exists in.
     */
    public List<Integer> getListOfNewsgroupIds() {
        return newsgroup_ids;
    }

    public Integer getPersonalLevel() {
        return personal_level;
    }

    /**
     * @return A sticky object.
     */
    public Sticky getSticky() {
        return sticky;
    }

    /**
     * @return The subject line of the post.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return The number of users who have starred this post, including the current user.
     */
    public Integer getStarsTotal() {
        return total_stars;
    }

    /**
     * @return If this post is unread for the current user,
     * specifies whether the unread-ness is "auto"
     * (marked unread automatically when first seen by WebNews) or "manual" (marked unread using the API).
     * If this post is not unread, the property will be null.
     */
    public String getUnreadClass() {
        return unread_class;
    }

    private class Sticky {

        private String username;

        private String display_name;

        private String expires_at;


        /**
         * @return Username of the user who last modified this post's stickiness.
         */
        public String getUserName() {
            return username;
        }

        /**
         * @return Displayname of the user who last modified this post's stickiness.
         */
        public String getDisplayName() {
            return display_name;
        }

        public String getExpiresAt() {
            return expires_at;
        }
    }

}
