package repository;

import domain.Entity;
import domain.Page;
import domain.Pageable;

public interface FriendshipsPagingRepo<ID, E extends Entity<ID>> extends PagingRepository<ID, E>{
    Page<E> findAllFriendRequests(Pageable pageable);

    Page<E> findAllUserFriends(Pageable pageable, Long id);

    Page<E> findAllUserFriendRequests(Pageable pageable, Long id);
}

