package repository;

import java.util.HashMap;
import java.util.Map;
import domain.Entity;
import domain.validators.ValidationException;
import domain.validators.Validator;

/**
 * In-memory implementation of a generic repository for managing entities.
 * @param <ID> the type of the unique identifier for the entities
 * @param <E> the type of entities stored in the repository, extending Entity<ID>
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private Validator<E> validator; // Validator for validating entities
    protected Map<ID, E> entities; // Map to store entities with their unique identifiers

    /**
     * Constructor for InMemoryRepository.
     * @param validator the validator used for validating entities before saving or updating
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        this.entities = new HashMap<>();
    }

    /**
     * Finds an entity by its unique identifier.
     * @param id the unique identifier of the entity to find
     * @return the entity with the specified ID, or null if not found
     * @throws IllegalArgumentException if the provided ID is null
     */
    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        } else {
            return this.entities.get(id);
        }
    }

    /**
     * Retrieves all entities in the repository.
     * @return an iterable collection of all entities
     */
    @Override
    public Iterable<E> findAll() {
        return this.entities.values();
    }

    /**
     * Saves a new entity to the repository.
     * @param entity the entity to save
     * @return the entity if it already exists, null if it was successfully saved
     * @throws IllegalArgumentException if the entity is null
     * @throws ValidationException if the entity is invalid
     */
    @Override
    public E save(E entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITY CANNOT BE NULL");
        } else {
            this.validator.validate(entity); // Validate the entity before saving
            if (this.entities.containsKey(entity.getId())) {
                return entity; // Entity already exists
            } else {
                this.entities.put(entity.getId(), entity); // Save the new entity
                return null; // Indicate successful save
            }
        }
    }

    /**
     * Deletes an entity from the repository by its unique identifier.
     * @param id the unique identifier of the entity to delete
     * @return the deleted entity, or null if not found
     * @throws IllegalArgumentException if the provided ID is null
     */
    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null!");
        } else {
            return this.entities.remove(id); // Remove and return the entity
        }
    }

    /**
     * Updates an existing entity in the repository.
     * @param entity the entity with updated information
     * @return the updated entity if it was found and updated, null if not found
     * @throws IllegalArgumentException if the entity is null
     * @throws ValidationException if the entity is invalid
     */
    @Override
    public E update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null!");
        } else {
            this.validator.validate(entity); // Validate the entity before updating
            if (this.entities.containsKey(entity.getId())) {
                this.entities.put(entity.getId(), entity); // Update the existing entity
                return entity; // Return the updated entity
            } else {
                return null; // Entity not found
            }
        }
    }
}
