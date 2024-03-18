package com.vp.alf.component.event.repository;

import com.vp.alf.component.event.model.dao.AlfEventDao;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlfEventRepository extends ReactiveCrudRepository<AlfEventDao, Long>, AlfEventRepositoryOperation {
}
