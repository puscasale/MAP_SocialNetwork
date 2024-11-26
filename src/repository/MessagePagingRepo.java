package repository;

import domain.Entity;
import domain.Page;
import domain.Pageable;

public interface MessagePagingRepo<ID,E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAll(Pageable pageable, Long user1Id, Long user2Id);
}
