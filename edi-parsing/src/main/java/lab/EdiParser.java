package lab;

import java.util.ArrayList;
import java.util.List;

/** Minimal EDI readers that map a purchase order (X12 850 or EDIFACT ORDERS) to the shared Orders model. */
public final class EdiParser {
    public record Line(String sku, int quantity) {}

    public record Order(String orderId, List<Line> lines) {}

    private EdiParser() {}

    /** X12: segments end with '~', elements are separated by '*'. BEG03 = PO number, PO1 = line (PO102 qty, PO107 sku). */
    public static Order parseX12(String edi) {
        String id = null;
        List<Line> lines = new ArrayList<>();
        for (String[] e : split(edi, "~", "\\*")) {
            switch (e[0]) {
                case "BEG" -> id = e[3];
                case "PO1" -> lines.add(new Line(e[7], Integer.parseInt(e[2])));
                default -> { }
            }
        }
        return build(id, lines);
    }

    /** EDIFACT: segments end with '\'', elements are separated by '+', components by ':'. BGM02 = order no, LIN/QTY pairs. */
    public static Order parseEdifact(String edi) {
        String id = null;
        String sku = null;
        List<Line> lines = new ArrayList<>();
        for (String[] e : split(edi, "'", "\\+")) {
            switch (e[0]) {
                case "BGM" -> id = e[2];
                case "LIN" -> sku = e[3].split(":")[0];
                case "QTY" -> lines.add(new Line(sku, Integer.parseInt(e[1].split(":")[1])));
                default -> { }
            }
        }
        return build(id, lines);
    }

    /** Picks the parser from the interchange header: ISA = X12, UNB/UNA = EDIFACT. */
    public static Order parse(String edi) {
        String t = edi.stripLeading();
        if (t.startsWith("ISA")) return parseX12(t);
        if (t.startsWith("UNB") || t.startsWith("UNA")) return parseEdifact(t);
        throw new IllegalArgumentException("unknown EDI dialect");
    }

    private static Order build(String id, List<Line> lines) {
        if (id == null || lines.isEmpty()) throw new IllegalArgumentException("not a purchase order");
        return new Order(id, lines);
    }

    private static List<String[]> split(String edi, String segmentEnd, String elementSep) {
        List<String[]> out = new ArrayList<>();
        for (String seg : edi.replace("\r", "").replace("\n", "").split(java.util.regex.Pattern.quote(segmentEnd))) {
            if (!seg.isBlank()) out.add(seg.strip().split(elementSep, -1));
        }
        return out;
    }
}
