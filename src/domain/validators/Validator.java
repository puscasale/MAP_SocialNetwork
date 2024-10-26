package domain.validators;

/**
 * Interface for validating objects of a specified type.
 * @param <T> the type of object to validate
 */
public interface Validator<T> {
    /**
     * Validates an object of type T.
     * @param var1 the object to validate
     * @throws ValidationException if the object is invalid
     */
    void validate(T var1) throws ValidationException;
}
