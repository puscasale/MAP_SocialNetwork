package repository;

import domain.*;

public interface FriendshipPagingRepo<ID, E extends Entity<ID>> extends PagingRepo<ID, E> {

    /**
     * Retrieves a page of friends for a given user, based on the pagination details.
     *
     * @param pageable The pagination details (including the page number and page size).
     * @param user The user whose friends are to be retrieved.
     * @return A Page object containing a subset of friends and the total number of friends for the user.
     */
    Page<E> getUsersFriends(Pageable pageable, User user);
}
