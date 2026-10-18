package pt.ipp.isep.dei.ui.console.menu.actions;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.controller.ReturnedGoodsController;
import pt.ipp.isep.dei.ESINF.domain.ReturnedGoods;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Unit test for ReturnedGoodsUI.
 * - Inject a mock controller to control returned goods data.
 * - Capture System.out and assert expected formatted output is printed.
 */
public class ReturnedGoodsUITest {

    @Test
    public void run_printsReturnedGoodsList() throws Exception {
        ReturnedGoodsUI ui = new ReturnedGoodsUI();

        // Create mock controller and controlled returned goods list
        ReturnedGoodsController mockController = mock(ReturnedGoodsController.class);
        List<ReturnedGoods> list = new ArrayList<>();
        list.add(new ReturnedGoods("R1", new Date(0), "SKU-A", 4, true));
        when(mockController.manageReturnedGoods()).thenReturn(list);

        // Inject mock controller into private final 'controller' field via reflection
        Field controllerField = ReturnedGoodsUI.class.getDeclaredField("controller");
        controllerField.setAccessible(true);
        controllerField.set(ui, mockController);

        // Capture System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        try {
            ui.run();
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();

        // Assert the output contains expected parts of the formatted line
        assertTrue(output.contains("returnId=R1"));
        assertTrue(output.contains("sku=SKU-A"));
        assertTrue(output.contains("Restocked") || output.contains("Discarded"));
        assertTrue(output.contains("quantity=4"));
    }
}