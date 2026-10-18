package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.ReturnedGoods;
import pt.ipp.isep.dei.ESINF.services.impl.ReturnedGoodsServiceImpl;

import java.util.List;

/**
 * Controller for Returned Goods functionality.
 * Delegates processing of returned goods to the service layer.
 * Provides a simple façade method for the UI or other controllers to obtain handled returned goods.
 */
public class ReturnedGoodsController {
    /**
     * Service implementation responsible for handling returned goods business logic.
     * Instantiated here for simplicity; consider using dependency injection for testability.
     */
    private final ReturnedGoodsServiceImpl service = new ReturnedGoodsServiceImpl();

    /**
     * Controller for Returned Goods functionality.
     * Delegates processing of returned goods to the service layer.
     * Provides a simple façade method for the UI or other controllers to obtain handled returned goods.
     */
    public List<ReturnedGoods> manageReturnedGoods() {
        return service.handleReturnedGoods();
    }

}
