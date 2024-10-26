package repository;

import domain.Entity;
import domain.validators.ValidationException;
import domain.validators.Validator;

import java.io.*;
import java.util.Optional;

/**
 * Abstract class for a file-based repository that extends the in-memory repository.
 * Provides methods for loading and saving entities to a file.
 * @param <ID> the type of the unique identifier for the entities
 * @param <E> the type of entities stored in the repository, extending Entity<ID>
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename; // The name of the file where entities are stored

    /**
     * Constructor for AbstractFileRepository.
     * @param validator the validator used for validating entities before saving or updating
     * @param fileName the name of the file to load and save data
     */
    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.filename = fileName;
        this.loadData(); // Load existing data from the file
    }

    /**
     * Loads entities from the file and adds them to the repository.
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filename))) {
            String line;
            // Read each line from the file and create an entity
            while ((line = br.readLine()) != null) {
                E entity = this.createEntity(line);
                super.save(entity); // Save the entity to the in-memory repository
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for any IO exception
        }
    }

    /**
     * Abstract method to create an entity from a string representation.
     * @param line the string representation of the entity
     * @return the created entity
     */
    public abstract E createEntity(String line);

    /**
     * Abstract method to get the string representation of an entity for saving to a file.
     * @param entity the entity to save
     * @return the string representation of the entity
     */
    public abstract String saveEntity(E entity);

    /**
     * Finds an entity by its unique identifier.
     * @param id the unique identifier of the entity to find
     * @return the entity with the specified ID, or null if not found
     */
    @Override
    public Optional<E> findOne(ID id) {
        return super.findOne(id); // Delegate to the in-memory repository
    }

    /**
     * Retrieves all entities in the repository.
     * @return an iterable collection of all entities
     */
    @Override
    public Iterable<E> findAll() {
        return super.findAll(); // Delegate to the in-memory repository
    }

    /**
     * Saves a new entity to the repository.
     * @param entity the entity to save
     * @return the entity if it already exists, null if it was successfully saved
     * @throws ValidationException if the entity is invalid
     */
    @Override
    public Optional<E> save(E entity) {
        Optional<E> existingEntity = super.save(entity); // Save the entity in memory
        if (existingEntity == null) {
            this.writeToFile(); // Write changes to the file if saved successfully
        }
        return existingEntity; // Return the existing entity or null
    }

    /**
     * Writes all entities in the repository to the file.
     */
    private void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename))) {
            for (E entity : this.entities.values()) {
                String entityStr = this.saveEntity(entity); // Get string representation of the entity
                writer.write(entityStr); // Write to the file
                writer.newLine(); // Add a new line
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // Throw a runtime exception if writing fails
        }
    }

    /**
     * Deletes an entity from the repository by its unique identifier.
     * @param id the unique identifier of the entity to delete
     * @return the deleted entity, or null if not found
     */
    @Override
    public Optional<E> delete(ID id) {
        Optional<E> deletedEntity = super.delete(id); // Delete from the in-memory repository
        if (deletedEntity != null) {
            System.out.println(deletedEntity);
            this.writeToFile(); // Write changes to the file if deleted successfully
        }
        return deletedEntity; // Return the deleted entity or null
    }

    /**
     * Updates an existing entity in the repository.
     * @param entity the entity with updated information
     * @return the updated entity if it was found and updated, null if not found
     */
    @Override
    public Optional<E> update(E entity) {
        Optional<E> updatedEntity = super.update(entity); // Update in the in-memory repository
        if (updatedEntity == null) {
            this.writeToFile(); // Write changes to the file if updated successfully
        }
        return updatedEntity; // Return the updated entity or null
    }
}
