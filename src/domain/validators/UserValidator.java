package domain.validators;

import domain.User;

/**
 * Validator class for User entities, implementing the Validator interface.
 * Checks if a User instance has valid fields.
 */
public class UserValidator implements Validator<User> {

    /**
     * Default constructor for UserValidator.
     */
    public UserValidator() {
    }

    /**
     * Validates a User entity by checking if the first name, last name, and ID are valid.
     * @param entity the User entity to validate
     * @throws ValidationException if any validation rule is violated
     */
    @Override
    public void validate(User entity) throws ValidationException {
        // Check if the first name is empty
        StringBuilder err = new StringBuilder();
        if (entity.getFirstName().isEmpty()) {
            err.append("First name is required\n");
        }

        // Check if the last name is empty
        if (entity.getLastName().isEmpty()) {
            err.append("Last name is required\n");
        }

        //Check if the id is empty and a number
        if(entity.getId() == null) {
            err.append("ID is required\n");
        } else {
            try{
                Long.parseLong(String.valueOf(entity.getId()));
            } catch (NumberFormatException e) {
                err.append("ID is not a number\n");
            }
        }
        if (!err.isEmpty()) {
            throw new ValidationException(err.toString());
        }
    }
}
