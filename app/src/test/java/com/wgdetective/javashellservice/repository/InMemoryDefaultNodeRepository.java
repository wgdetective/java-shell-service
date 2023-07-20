// package com.wgdetective.javashellservice.repository;
//
// import java.util.HashMap;
//
// import com.wgdetective.javashellservice.exception.NodeNotFoundException;
// import com.wgdetective.javashellservice.model.Node;
// import com.wgdetective.javashellservice.repositiry.NodeRepository;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
//// @Repository
//// @Profile()
// public class InMemoryDefaultNodeRepository implements NodeRepository {
//
// private final HashMap<Long, Node> data = new HashMap<>();
//
// private long nextId = 1L;
//
// private final Object lock = new Object();
//
// @Override
// public Mono<Node> findById(final Long id) {
// return data.containsKey(id) ? Mono.just(data.get(id)) : Mono.empty();
// }
//
// @Override
// public Flux<Node> findAll() {
// return Flux.fromIterable(data.values());
// }
//
// @Override
// public Mono<Node> save(final Node node) {
// if (node.id() == null) {
// synchronized (lock) {
// final var newNode = new Node(nextId, node.parentId(), node.value());
// data.put(nextId, newNode);
// nextId++;
// return Mono.just(newNode);
// }
// } else {
// if (data.containsKey(node.id())) {
// throw new NodeNotFoundException("Node with id=" + node.id() + " not found");
// } else {
// synchronized (lock) {
// data.put(node.id(), node);
// return Mono.just(node);
// }
// }
// }
// }
//
// @Override
// public Mono<Void> deleteById(final Long id) {
// final var result = data.containsKey(id);
// synchronized (lock) {
// data.remove(id);
// }
// return Mono.just(result).then();
// }
//
// }
