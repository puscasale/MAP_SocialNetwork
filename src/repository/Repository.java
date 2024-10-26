package repository;

import domain.Entity;

/**
 * Interface for a generic repository to perform CRUD operations on entities.
 * @param <ID> the type of the unique identifier for the entities
 * @param <E> the type of entities stored in the repository, extending Entity<ID>
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Finds an entity by its unique identifier.
     * @param id the unique identifier of the entity to find
     * @return the entity with the specified ID, or null if not found
     */
    E findOne(ID id);

    /**
     * Retrieves all entities in the repository.
     * @return an iterable collection of all entities
     */
    Iterable<E> findAll();

    /**
     * Saves a new entity to the repository.
     * @param entity the entity to save
     * @return the saved entity
     */
    E save(E entity);

    /**
     * Deletes an entity from the repository by its unique identifier.
     * @param id the unique identifier of the entity to delete
     * @return the deleted entity, or null if not found
     */
    E delete(ID id);

    /**
     * Updates an existing entity in the repository.
     * @param entity the entity with updated information
     * @return the updated entity
     */
    E update(E entity);
}
