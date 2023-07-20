package com.wgdetective.javashellservice.repositiry;

import com.wgdetective.javashellservice.repositiry.entity.NodeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Reactive database node repository.
 */
@Repository
public interface DataBaseNodeRepository extends ReactiveCrudRepository<NodeEntity, Long> {

}
