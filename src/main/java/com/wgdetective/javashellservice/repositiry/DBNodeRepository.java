package com.wgdetective.javashellservice.repositiry;

import com.wgdetective.javashellservice.repositiry.entity.NodeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBNodeRepository extends ReactiveCrudRepository<NodeEntity, Long> {

}
