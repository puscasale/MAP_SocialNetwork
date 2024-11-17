package domain;

import enums.Friendshiprequest;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a Friendship between two users, each identified by a unique Long ID.
 * Extends the Entity class with Tuple<Long, Long> as the ID type.
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    private LocalDateTime date;
    private Long idUser1;
    private Long idUser2;
    Friendshiprequest friendshiprequest;

    /**
     * Constructs a Friendship instance between two users with specified IDs.
     * @param idUser1 the ID of the first user in the friendship
     * @param idUser2 the ID of the second user in the friendship
     */
    public Friendship(Long idUser1, Long idUser2, LocalDateTime date) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.date = date;
        this.friendshiprequest = Friendshiprequest.PENDING;
    }

    /**
     * Constructs a Friendship instance between two users with specified IDs.
     * @param idUser1 the ID of the first user in the friendship
     * @param idUser2 the ID of the second user in the friendship
     */
    public Friendship(Long idUser1, Long idUser2, LocalDateTime date, Friendshiprequest friendshiprequest) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.date = date;
        this.friendshiprequest = friendshiprequest;
    }

    public Friendshiprequest getFriendshiprequest() {
        return friendshiprequest;
    }

    public void setFriendshiprequest(Friendshiprequest friendshiprequest) {
        this.friendshiprequest = friendshiprequest;
    }


    /**
     * Gets the ID of the first user in the friendship.
     * @return the ID of the first user
     */
    public Long getIdUser1() {
        return idUser1;
    }

    /**
     * Gets the ID of the second user in the friendship.
     * @return the ID of the second user
     */
    public Long getIdUser2() {
        return idUser2;
    }

    /**
     * Gets the date when the friendship was established.
     * @return the date of the friendship
     */
    public LocalDateTime getDate() {
        return this.date;
    }


    /**
     * Checks if this Friendship object is equal to another object.
     * Two Friendship objects are considered equal if they have the same ID.
     *
     * @param o the object to compare with this Friendship
     * @return true if the given object is also a Friendship with the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Checks if both references point to the same object
        if (!(o instanceof Friendship)) return false; // Checks if o is of the same type
        Friendship that = (Friendship) o;
        return Objects.equals(getId(), that.getId()); // Compares IDs for equality
    }

    /**
     * Generates a hash code for this Friendship object based on its ID.
     * The hash code is used in hashing data structures such as HashMap.
     *
     * @return the hash code value for this Friendship object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId()); // Generates hash based on the ID of the friendship
    }

    @Override
    public String toString() {
        return "Friendship " +
                "userId1: " + idUser1 +
                ", userId2: " + idUser2 + "date: " + date.toLocalDate();
    }


}
