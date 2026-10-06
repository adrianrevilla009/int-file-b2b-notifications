package lab;

import java.util.ArrayList;
import java.util.List;

/** Sends an order notification through the first channel that works; failures fall through to the next one. */
public class NotificationService {
    /** A delivery channel stub. Throw to signal that delivery failed. */
    public interface Channel {
        String name();

        void send(String recipient, String message) throws Exception;
    }

    public record Result(String channel, List<String> failed) {
        public boolean delivered() {
            return channel != null;
        }
    }

    private final List<Channel> channels;

    /** Channels in priority order, e.g. email, SMS, push. */
    public NotificationService(List<Channel> channels) {
        this.channels = List.copyOf(channels);
    }

    public Result notifyRecipient(String recipient, String message) {
        List<String> failed = new ArrayList<>();
        for (Channel c : channels) {
            try {
                c.send(recipient, message);
                return new Result(c.name(), failed);
            } catch (Exception e) {
                failed.add(c.name());
            }
        }
        return new Result(null, failed);
    }

    /** In-memory stub: records messages, or fails on demand to simulate an outage. */
    public static class StubChannel implements Channel {
        private final String name;
        private final boolean up;
        public final List<String> sent = new ArrayList<>();

        public StubChannel(String name, boolean up) {
            this.name = name;
            this.up = up;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void send(String recipient, String message) throws Exception {
            if (!up) throw new Exception(name + " unavailable");
            sent.add(recipient + ": " + message);
        }
    }
}
