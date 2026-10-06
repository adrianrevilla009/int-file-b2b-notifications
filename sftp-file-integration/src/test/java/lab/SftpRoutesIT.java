package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class SftpRoutesIT {
    @Container
    static final GenericContainer<?> SFTP = new GenericContainer<>("atmoz/sftp:alpine-3.7")
        .withCommand("partner:secret:::upload/inbox")
        .withExposedPorts(22)
        .waitingFor(Wait.forListeningPort());

    @Test
    void processesEachFileOnce() throws Exception {
        try (CamelContext ctx = new DefaultCamelContext()) {
            ctx.addRoutes(new SftpRoutes(SFTP.getHost(), SFTP.getMappedPort(22)));
            MockEndpoint mock = ctx.getEndpoint("mock:orders", MockEndpoint.class);
            mock.expectedMessageCount(2);
            ctx.start();
            put("order-1.csv", "O-1,widget,2");
            put("order-2.csv", "O-2,gadget,1");
            mock.assertIsSatisfied(15_000);
            Thread.sleep(2_000); // more polls: noop=true keeps the files, idempotency must suppress them
            assertEquals(2, mock.getReceivedCounter());
        }
    }

    private static void put(String name, String body) {
        SFTP.copyFileToContainer(Transferable.of(body.getBytes(StandardCharsets.UTF_8), 0666), "/home/partner/upload/inbox/" + name);
    }
}
