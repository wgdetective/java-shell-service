package com.wgdetective.javashellservice.controller;

import com.wgdetective.javashellservice.controller.dto.CreateNodeDto;
import com.wgdetective.javashellservice.controller.dto.NodeDto;
import com.wgdetective.javashellservice.controller.mapper.NodeDtoMapper;
import com.wgdetective.javashellservice.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * NodeController.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/node")
public class NodeController {

    private final NodeService service;

    private final NodeDtoMapper mapper;

    @GetMapping("/{id}")
    public Mono<NodeDto> get(@PathVariable final Long id) {
        return service.getById(id).map(mapper::map);
    }

    @GetMapping
    public Flux<NodeDto> getAll() {
        return service.getAll().map(mapper::map);
    }

    @PutMapping
    public Mono<NodeDto> save(@RequestBody CreateNodeDto node) {
        return service.save(mapper.map(node)).map(mapper::map);
    }

    @PostMapping
    public Mono<NodeDto> update(@RequestBody NodeDto node) {
        return service.update(mapper.map(node)).map(mapper::map);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable final Long id) {
        return service.delete(id);
    }

}
