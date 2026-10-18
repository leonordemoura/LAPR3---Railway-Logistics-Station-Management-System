package pt.ipp.isep.dei.ESINF.repository;

import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;

/**
 * Inspired on <a href="https://refactoring.guru/design-patterns/singleton/java/example">...</a>
 * The Repositories class works as a Singleton. It defines the getInstance method that serves as an alternative
 * to the constructor and lets client classes access the same instance of this class over and over.
 */
public class Repositories {
    private static Repositories instance;
    private final AuthenticationRepository authenticationRepository;
    private final WarehouseRepository warehouseRepository;
    private final RailwayRepository railwayRepository;

    /**
     * The Singleton's constructor should always be private to prevent direct construction calls with the new operator.
     */
    private Repositories() {
         authenticationRepository = new AuthenticationRepository();
         warehouseRepository = new WarehouseRepository();
         railwayRepository = new RailwayRepository();
    }

    /**
     * This is the static method that controls the access to the singleton instance.
     * On the first run, it creates a singleton object and places it into the static attribute.
     * On subsequent runs, it returns the existing object stored in the static attribute.
     */
    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                instance = new Repositories();

            }
        }
        return instance;
    }

    /**
     * Returns the authentication repository instance.
     *
     * @return the AuthenticationRepository instance
     */
    public AuthenticationRepository getAuthenticationRepository() {
        return authenticationRepository;
    }

    /**
     * Returns the warehouse repository instance.
     *
     * @return the WarehouseRepository instance
     */
    public WarehouseRepository getWarehouseRepository() {
        return warehouseRepository;
    }

    /**
     * Returns the railway repository instance.
     *
     * @return the RailwayRepository instance
     */
    public RailwayRepository getRailwayRepository() { return railwayRepository;}

    /**
     * Resets the singleton instance by creating a new Repositories object.
     */
    public static void reset() {instance = new Repositories();}
}