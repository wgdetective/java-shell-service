package com.wgdetective.javashellservice.repository;

import com.wgdetective.javashellservice.repository.entity.NodeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Reactive database node repository.
 */
@Repository
public interface DataBaseNodeRepository extends ReactiveCrudRepository<NodeEntity, Long> {

}
