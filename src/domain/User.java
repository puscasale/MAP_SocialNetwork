package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a User with a first name, last name, and a list of friends.
 * Extends the Entity class with Long as the ID type.
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<User> friends;

    /**
     * Constructs a User with the specified first name and last name.
     * Initializes the friends list as an empty ArrayList.
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    /**
     * Gets the first name of the user.
     * @return the first name of the user
     */
    public String getFirstName() {
        return this.firstName;
    }

    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the first name of the user.
     * @param firstName the new first name for the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     * @return the last name of the user
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the last name of the user.
     * @param lastName the new last name for the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retrieves the list of friends of the user.
     * @return a list containing the user's friends
     */
    public List<User> getFriends() {
        return this.friends;
    }

    /**
     * Removes a user from the friends list.
     * @param u the user to remove from the friends list
     */
    public void removeFriend(User u) {
        friends.remove(u);
    }

    /**
     * Adds a user to the friends list.
     * @param u the user to add to the friends list
     */
    public void addFriend(User u) {
        friends.add(u);
    }

    /**
     * Provides a string representation of the user, including their name and friends list.
     * @return a formatted string with the user's name and friends
     */
    public String toString() {
        StringBuilder friendsList = new StringBuilder();
        for (User friend : friends) {
            if (friend != null) {
                friendsList.append(friend.getFirstName())
                        .append(" ")
                        .append(friend.getLastName())
                        .append(", ");
            }
        }
        return "User " + " id: " + this.getId()+
                " firstName: " + firstName + '\'' +
                " lastName: " + lastName + '\'' + "email: " + email + '\'';
    }

    /**
     * Compares this user to another object for equality.
     * Two users are equal if their first names, last names, and friends lists are equal.
     * @param o the object to compare with this user
     * @return true if the specified object is equal to this user, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof User)) {
            return false;
        } else {
            User that = (User) o;
            return Objects.equals(getId(), that.getId());
        }
    }

    /**
     * Generates a hash code for the user based on their name and friends list.
     * @return a hash code value for this user
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
