package com.wgdetective.javashellservice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.wgdetective.javashellservice.controller.dto.CreateNodeDto;
import com.wgdetective.javashellservice.repositiry.DBNodeRepository;
import com.wgdetective.javashellservice.repositiry.entity.NodeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("local")
public class NodeCRUDUseCaseTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private DBNodeRepository repository;

    @Test
    void saveNode() throws Exception {
        // given
        final var expectedJson = readResource("bdd/saveNode.json");
        var node = new CreateNodeDto(null, "Value");

        // when
        client.put().uri("/v1/node")
                .bodyValue(node)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);

        // clean up
        repository.delete(repository.findAll().blockFirst()).block();
    }

    @Test
    void updateNode() throws Exception {
        // given
        final var expectedJson = readResource("bdd/updateNode.json");
        var node = new NodeEntity();
        node.setValue("Value");
        node = repository.save(node).block();

        var updateNode = new NodeEntity();
        updateNode.setValue("Value2");
        updateNode = repository.save(updateNode).block();

        // when
        client.post().uri("/v1/node")
                .bodyValue(updateNode)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);

        // clean up
        repository.delete(repository.findAll().blockFirst()).block();
    }

    @Test
    void testGetNodeById() throws Exception {
        // given
        final var expectedJson = readResource("bdd/getNodeById.json");
        var node = new NodeEntity();
        node.setValue("Value");
        node = repository.save(node).block();

        // when
        client.get().uri("/v1/node/" + node.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);

        // clean up
        repository.delete(node).block();
    }

    @Test
    void testFindAllNodes() throws Exception {
        // given
        final var expectedJson = readResource("bdd/findAllNodes.json");
        var node1 = new NodeEntity();
        node1.setValue("Value1");
        node1 = repository.save(node1).block();
        var node2 = new NodeEntity();
        node2.setValue("Value2");
        node2 = repository.save(node2).block();

        // when
        client.get().uri("/v1/node")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);

        // clean up
        repository.delete(node1).block();
        repository.delete(node2).block();
    }

    private String readResource(final String resourceName) throws IOException {
        try (final var resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resourceName)) {
            return new String(Objects.requireNonNull(resourceAsStream).readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
