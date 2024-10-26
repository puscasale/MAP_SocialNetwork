package domain;

/**
 * Generic Entity class defining a basic entity with an identifier of type ID.
 * @param <ID> the type of the unique identifier for the entity
 */
public class Entity<ID> {
    private ID id;

    /**
     * Default constructor for the Entity class.
     */
    public Entity() {
    }

    /**
     * Method to get the entity's identifier.
     * @return the unique identifier of the entity of specified type ID
     */
    public ID getId() {
        return this.id;
    }

    /**
     * Method to set the entity's identifier.
     * @param id the unique identifier of the entity of specified type ID
     */
    public void setId(ID id) {
        this.id = id;
    }
}
