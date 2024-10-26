package repository;

import domain.User;
import domain.validators.Validator;

/**
 * Repository for managing User entities, extending the AbstractFileRepository.
 * Provides methods for creating and saving User entities from/to a file.
 */
public class UserRepository extends AbstractFileRepository<Long, User> {
    /**
     * Constructor for UserRepository.
     * @param validator the validator used for validating User entities before saving or updating
     * @param fileName the name of the file to load and save User data
     */
    public UserRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    /**
     * Creates a User entity from a string representation.
     * @param line the string representation of the User in the format "id;firstName;lastName"
     * @return the created User entity
     */
    @Override
    public User createEntity(String line) {
        String[] split = line.split(";"); // Split the line by semicolon
        User user = new User(split[1], split[2]); // Create User with first and last name
        user.setId(Long.parseLong(split[0])); // Set the ID of the User
        return user; // Return the created User
    }

    /**
     * Gets the string representation of a User entity for saving to a file.
     * @param entity the User entity to save
     * @return the string representation of the User in the format "id;firstName;lastName"
     */
    @Override
    public String saveEntity(User entity) {
        String id = String.valueOf(entity.getId()); // Get the ID of the User as a string
        String s = id + ";" + entity.getFirstName() + ";" + entity.getLastName(); // Create string representation
        return s; // Return the string representation
    }
}
