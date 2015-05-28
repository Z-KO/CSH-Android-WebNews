/*
 * Represents the current user
 * https://github.com/grantovich/CSH-WebNews/wiki/API%3A-The-Current-User
 *
 * Peter Zujko
 */

package edu.csh.cshwebnews.models;

public class User {

    private UserResponse user;

    /**
     * @return The user's authenticated username.
     */
    public String getUserName(){
        return user.getUsername();
    }

    /**
     * @return The user's email address.
     */
    public String getEmail(){
        return user.getEmail();
    }

    /**
     * @return The user's preferred display name ("Common Name").
     */
    public String getDisplayName(){
        return user.getDisplayName();
    }

    /**
     * @return When the user's WebNews account was created.
     */
    public String getCreatedAt(){
        return user.getCreatedAt();
    }

    public Boolean isAdmin(){
        return user.getIsAdmin();
    }


    private class UserResponse{

        private String username;

        private String email;

        private String display_name;

        private String created_at;

        private Boolean is_admin;


        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getDisplayName() {
            return display_name;
        }

        public String getCreatedAt() {
            return created_at;
        }

        public Boolean getIsAdmin() {
            return is_admin;
        }
    }
}


