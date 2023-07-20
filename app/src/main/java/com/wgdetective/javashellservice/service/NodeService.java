package com.wgdetective.javashellservice.service;

import com.wgdetective.javashellservice.model.CreateNode;
import com.wgdetective.javashellservice.model.Node;
import com.wgdetective.javashellservice.repositiry.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository repository;

    public Mono<Node> getById(final Long id) {
        return repository.findById(id);
    }

    public Flux<Node> getAll() {
        return repository.findAll();
    }

    public Mono<Node> save(final CreateNode node) {
        return repository.save(new Node(null, node.parentId(), node.value()));
    }

    public Mono<Node> update(final Node node) {
        return repository.save(node);
    }

    public Mono<Void> delete(final Long id) {
        return repository.deleteById(id);
    }

}
