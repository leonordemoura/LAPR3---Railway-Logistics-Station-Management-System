package pt.ipp.isep.dei.ESINF.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.ReturnedGoods;
import pt.ipp.isep.dei.ESINF.services.impl.ReturnedGoodsServiceImpl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

/**
 * Test class for ReturnedGoodsController
 * Verifies delegation to the service layer and correct return of service results.
 */
class ReturnedGoodsControllerTest {

    @Test
    @DisplayName("manageReturnedGoods delegates to service and returns the service result")
    void manageReturnedGoods_delegatesToService() throws Exception {
        ReturnedGoodsController controller = new ReturnedGoodsController();
        ReturnedGoodsServiceImpl mockService = mock(ReturnedGoodsServiceImpl.class);

        List<ReturnedGoods> expected = List.of(
                new ReturnedGoods("R1", new Date(), "SKU1", 1, true)
        );
        when(mockService.handleReturnedGoods()).thenReturn(expected);

        // Inject mock into the controller's private field
        Field serviceField = ReturnedGoodsController.class.getDeclaredField("service");
        serviceField.setAccessible(true);
        serviceField.set(controller, mockService);

        List<ReturnedGoods> result = controller.manageReturnedGoods();

        // Verifies if the controller returned exactly what the service produced and the service was invoked once
        assertSame(expected, result);
        verify(mockService, times(1)).handleReturnedGoods();
    }
}