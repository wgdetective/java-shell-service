package com.wgdetective.javashellservice.repositiry;

import com.wgdetective.javashellservice.model.Node;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NodeRepository {

    Mono<Node> findById(Long id);

    Flux<Node> findAll();

    Mono<Node> save(Node node);

    Mono<Void> deleteById(Long id);
}
