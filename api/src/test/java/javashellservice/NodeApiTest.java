package javashellservice;

import com.wgdetective.javashellservice.NodeApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NodeApiTest {

    @Test
    void test() {
        assertNotNull(new NodeApi().getAllNodes());
    }

}
