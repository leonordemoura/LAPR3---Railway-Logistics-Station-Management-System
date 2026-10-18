package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ReturnedGoods domain class
 * Tests basic getters and boolean flag behaviour for returned goods records
 */
class ReturnedGoodsTest {

    @Test
    @DisplayName("Getters return provided values")
    void gettersReturnProvidedValues() {
        Date ts = new Date();
        ReturnedGoods rg = new ReturnedGoods("R1", ts, "SKU-123", 5, true);

        //verify all fields are returned exactly as provided
        assertEquals("R1", rg.getReturnId());
        assertSame(ts, rg.getTimestamp());
        assertEquals("SKU-123", rg.getSku());
        assertEquals(5, rg.getQty());
        assertTrue(rg.getActionTaken());
    }

    @Test
    @DisplayName("Action flag can be false")
    void actionFlagFalse() {
        ReturnedGoods rg = new ReturnedGoods("R2", new Date(), "SKU-2", 1, false);

        //ensure the flag reflects the provided false value
        assertFalse(rg.getActionTaken());
    }
}