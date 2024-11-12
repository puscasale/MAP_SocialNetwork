package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;

import java.time.LocalDateTime;

/**
 * Repository for managing Friendship entities, extending the AbstractFileRepository.
 * Provides methods for creating and saving Friendship entities from/to a file.
 */
public class FriendshipRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    /**
     * Constructor for FriendshipRepository.
     * @param validator the validator used for validating Friendship entities before saving or updating
     * @param filename the name of the file to load and save Friendship data
     */
    public FriendshipRepository(Validator<Friendship> validator, String filename) {
        super(validator, filename);
    }

    /**
     * Creates a Friendship entity from a string representation.
     * @param line the string representation of the Friendship in the format "idUser1 idUser2"
     * @return the created Friendship entity
     */
    @Override
    public Friendship createEntity(String line) {
        Long id1 = Long.parseLong(line.split(" ")[0]); // Parse the first user ID from the string
        Long id2 = Long.parseLong(line.split(" ")[1]);// Parse the second user ID from the string
        LocalDateTime date = LocalDateTime.parse(line.split(" ")[2]);
        Friendship friendship = new Friendship(id1, id2, date); // Create Friendship with both user IDs
        friendship.setId(new Tuple<>(id1, id2)); // Set the composite ID using a Tuple
        return friendship; // Return the created Friendship
    }

    /**
     * Gets the string representation of a Friendship entity for saving to a file.
     * @param friendship the Friendship entity to save
     * @return the string representation of the Friendship in the format "idUser1 idUser2"
     */
    @Override
    public String saveEntity(Friendship friendship) {
        Long u1 = friendship.getIdUser1(); // Get the first user ID
        Long u2 = friendship.getIdUser2(); // Get the second user ID
        return u1.toString() + " " + u2.toString(); // Return the string representation
    }
}
