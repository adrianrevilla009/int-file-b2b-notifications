package lab;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;

/** Polls an SFTP inbox for order files; each file is processed once even though it stays in the inbox. */
public class SftpRoutes extends RouteBuilder {
    private final String host;
    private final int port;

    public SftpRoutes(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void configure() {
        // noop=true leaves files in place (the partner owns the inbox), so the idempotent consumer prevents duplicates.
        // Key = name + size: re-polling the same file is skipped.
        from("sftp://" + host + ":" + port + "/upload/inbox?username=partner&password=secret"
                + "&noop=true&idempotent=false&delay=500&initialDelay=0&useUserKnownHostsFile=false&strictHostKeyChecking=no")
            .routeId("orders-inbox")
            .idempotentConsumer(simple("${file:name}-${file:size}"), MemoryIdempotentRepository.memoryIdempotentRepository(1000))
            .log("processing ${file:name}")
            .to("mock:orders");
    }
}
