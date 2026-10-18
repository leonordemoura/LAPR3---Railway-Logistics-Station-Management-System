package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.Return;
import pt.ipp.isep.dei.ESINF.domain.ReturnedGoods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ReturnedGoodsServiceImpl
 * Verifies ordering, tie-break behaviour and business rules (restock vs discard)
 * Also validates that audit logging (file output) is triggered with expected content.
 */
class ReturnedGoodsServiceImplTest {
    private static class TestableService extends ReturnedGoodsServiceImpl {
        private final List<Return> source;
        final List<String> printedLines = new ArrayList<>();

        TestableService(List<Return> source) {
            this.source = source;
        }

        @Override
        public List<Return> getAllReturns() {
            return new ArrayList<>(source);
        }

        @Override
        public void printToFile(List<ReturnedGoods> returns) {
            for (ReturnedGoods r : returns) {
                String action = r.getActionTaken() ? "Restocked" : "Discarded";
                printedLines.add("returnId=" + r.getReturnId() + " action=" + action + " qty=" + r.getQty());
            }
        }
    }

    @Test
    @DisplayName("Newest first and action flag set")
    void newestFirstAndActionFlag() {
        //create two mocked returns with different timestamps and reasons
        Return newer = mock(Return.class);
        Return older = mock(Return.class);

        Date now = new Date();
        Date past = new Date(now.getTime() - 10_000);

        when(newer.getTimestamp()).thenReturn(now);
        when(newer.getReturnId()).thenReturn("N");
        when(newer.getSku()).thenReturn("S1");
        when(newer.getQty()).thenReturn(1);
        when(newer.getReason()).thenReturn("customer remorse"); // restock

        when(older.getTimestamp()).thenReturn(past);
        when(older.getReturnId()).thenReturn("O");
        when(older.getSku()).thenReturn("S2");
        when(older.getQty()).thenReturn(2);
        when(older.getReason()).thenReturn("expired"); // discard

        TestableService svc = new TestableService(List.of(older, newer));

        // process returned goods
        List<ReturnedGoods> out = svc.handleReturnedGoods();

        // newest appears first and flags follow business rules
        assertEquals(2, out.size());
        assertEquals("N", out.get(0).getReturnId());
        assertTrue(out.get(0).getActionTaken());
        assertEquals("O", out.get(1).getReturnId());
        assertFalse(out.get(1).getActionTaken());
    }

    @Test
    @DisplayName("Tie-break by returnId ascending")
    void tieBreakById() {
        // two returns with identical timestamps but different IDs
        Return a = mock(Return.class);
        Return b = mock(Return.class);
        Date t = new Date();

        when(a.getTimestamp()).thenReturn(t);
        when(a.getReturnId()).thenReturn("A");
        when(a.getSku()).thenReturn("SA");
        when(a.getQty()).thenReturn(1);
        when(a.getReason()).thenReturn("customer remorse");

        when(b.getTimestamp()).thenReturn(t);
        when(b.getReturnId()).thenReturn("B");
        when(b.getSku()).thenReturn("SB");
        when(b.getQty()).thenReturn(1);
        when(b.getReason()).thenReturn("customer remorse");

        TestableService svc = new TestableService(List.of(b, a)); // reverse input order

        // process returned goods
        List<ReturnedGoods> out = svc.handleReturnedGoods();

        // tie-break by returnId ascending
        assertEquals(2, out.size());
        assertEquals("A", out.get(0).getReturnId());
        assertEquals("B", out.get(1).getReturnId());
    }

    @Test
    @DisplayName("Business rules: restock/discard and audit logging")
    void restockDiscardAndAudit() {
        // prepare two returns, one restock and one discard, with timestamps
        Return r1 = mock(Return.class);
        Return r2 = mock(Return.class);

        Date t1 = new Date();
        Date t2 = new Date(t1.getTime() - 1_000);

        when(r1.getTimestamp()).thenReturn(t1);
        when(r1.getReturnId()).thenReturn("R1");
        when(r1.getSku()).thenReturn("S1");
        when(r1.getQty()).thenReturn(3);
        when(r1.getReason()).thenReturn("cycle count"); // restock

        when(r2.getTimestamp()).thenReturn(t2);
        when(r2.getReturnId()).thenReturn("R2");
        when(r2.getSku()).thenReturn("S2");
        when(r2.getQty()).thenReturn(5);
        when(r2.getReason()).thenReturn("expired"); // discard

        TestableService svc = new TestableService(List.of(r2, r1));

        // process returned goods and capture audit output via overridden method
        List<ReturnedGoods> out = svc.handleReturnedGoods();

        // verify action flags and that audit logging captured expected lines
        assertEquals(2, out.size());
        assertTrue(out.get(0).getActionTaken());  // R1 restocked (newest)
        assertFalse(out.get(1).getActionTaken()); // R2 discarded

        assertEquals(2, svc.printedLines.size());
        assertTrue(svc.printedLines.get(0).contains("returnId=R1"));
        assertTrue(svc.printedLines.get(1).contains("returnId=R2"));
    }
}