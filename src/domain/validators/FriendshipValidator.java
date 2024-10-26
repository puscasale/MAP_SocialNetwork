package domain.validators;

import domain.Friendship;

import java.util.Objects;

/**
 * Validator class for Friendship entities, implementing the Validator interface.
 * Checks if a Friendship instance has valid fields.
 */
public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Validates a Friendship entity by checking if the two user IDs are different.
     * @param entity the Friendship entity to validate
     * @throws ValidationException if the user IDs are the same
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        StringBuilder err = new StringBuilder();
        // Check if the two user IDs are identical, which would make the friendship invalid
        if (Objects.equals(entity.getIdUser1(), entity.getIdUser2())) {
            err.append("User1 is the same as user2 ");
        }
        if(entity.getIdUser1() == null || entity.getIdUser2() == null) {
            err.append("User ids cannot be null");
        }
        if(!err.isEmpty()) {
            throw new ValidationException(err.toString());
        }
    }
}
