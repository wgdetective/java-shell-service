package com.wgdetective.javashellservice.repository;

import com.wgdetective.javashellservice.model.Node;
import com.wgdetective.javashellservice.repository.mapper.NodeEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository wrapper.
 */
@Repository
@RequiredArgsConstructor
public class DataBaseNodeRepositoryWrapper implements NodeRepository {

    private final DataBaseNodeRepository repository;

    private final NodeEntityMapper mapper;

    @Override
    public Mono<Node> findById(final Long id) {
        return repository.findById(id).map(mapper::map);
    }

    @Override
    public Flux<Node> findAll() {
        return repository.findAll().map(mapper::map);
    }

    @Override
    public Mono<Node> save(final Node node) {
        return repository.save(mapper.map(node)).map(mapper::map);
    }

    @Override
    public Mono<Void> deleteById(final Long id) {
        return repository.deleteById(id);
    }

}
