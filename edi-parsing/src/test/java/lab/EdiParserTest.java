package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import lab.EdiParser.Line;
import lab.EdiParser.Order;
import org.junit.jupiter.api.Test;

class EdiParserTest {
    static final String X12 = "ISA*00*          *00*          *ZZ*PARTNER        *ZZ*US             *261004*1200*U*00401*000000001*0*T*>~\n"
        + "GS*PO*PARTNER*US*20261004*1200*1*X*004010~\nST*850*0001~\nBEG*00*NE*O-1001**20261004~\n"
        + "PO1*1*2*EA*9.99**VP*WIDGET~\nPO1*2*5*EA*1.50**VP*GADGET~\nSE*6*0001~\nGE*1*1~\nIEA*1*000000001~";

    static final String EDIFACT = "UNB+UNOA:1+PARTNER+US+261004:1200+1'UNH+1+ORDERS:D:96A:UN'BGM+220+O-1001+9'"
        + "LIN+1++WIDGET:SA'QTY+21:2'LIN+2++GADGET:SA'QTY+21:5'UNT+8+1'UNZ+1+1'";

    static final Order EXPECTED = new Order("O-1001", List.of(new Line("WIDGET", 2), new Line("GADGET", 5)));

    @Test
    void x12MapsToOrder() {
        assertEquals(EXPECTED, EdiParser.parse(X12));
    }

    @Test
    void edifactMapsToSameOrder() {
        assertEquals(EXPECTED, EdiParser.parse(EDIFACT));
    }

    @Test
    void rejectsUnknownAndIncomplete() {
        assertThrows(IllegalArgumentException.class, () -> EdiParser.parse("hello"));
        assertThrows(IllegalArgumentException.class, () -> EdiParser.parse("ISA*00~ST*850*1~"));
    }
}
