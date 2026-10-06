package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import lab.NotificationService.StubChannel;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {
    @Test
    void usesFirstChannelWhenUp() {
        var email = new StubChannel("email", true);
        var sms = new StubChannel("sms", true);
        var r = new NotificationService(List.of(email, sms)).notifyRecipient("a@x.io", "Order O-1 received");
        assertEquals("email", r.channel());
        assertTrue(r.failed().isEmpty());
        assertTrue(sms.sent.isEmpty());
    }

    @Test
    void fallsBackPastFailedChannels() {
        var push = new StubChannel("push", true);
        var r = new NotificationService(List.of(new StubChannel("email", false), new StubChannel("sms", false), push))
            .notifyRecipient("u1", "Order O-1 received");
        assertEquals("push", r.channel());
        assertEquals(List.of("email", "sms"), r.failed());
        assertEquals(1, push.sent.size());
    }

    @Test
    void reportsUndeliveredWhenAllFail() {
        var r = new NotificationService(List.of(new StubChannel("email", false), new StubChannel("sms", false)))
            .notifyRecipient("u1", "hi");
        assertFalse(r.delivered());
        assertEquals(List.of("email", "sms"), r.failed());
    }
}
