package repository;

import domain.Entity;
import domain.Page;
import domain.Pageable;

public interface PagingRepo<ID, E extends Entity<ID>> extends Repository<ID, E> {

    /**
     * Retrieves a page of entities from the repository based on the given pagination details.
     *
     * @param pageable The pagination details (including the page number and page size).
     * @return A Page object containing a subset of entities and the total number of elements.
     */
    public Page<E> findAllOnPage(Pageable pageable);
}
