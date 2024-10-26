package domain;

import java.time.LocalDateTime;

/**
 * Represents a Friendship between two users, each identified by a unique Long ID.
 * Extends the Entity class with Tuple<Long, Long> as the ID type.
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    private LocalDateTime date;
    private Long idUser1;
    private Long idUser2;

    /**
     * Constructs a Friendship instance between two users with specified IDs.
     * @param idUser1 the ID of the first user in the friendship
     * @param idUser2 the ID of the second user in the friendship
     */
    public Friendship(Long idUser1, Long idUser2) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
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
}
