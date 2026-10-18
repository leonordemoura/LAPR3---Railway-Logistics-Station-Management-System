package pt.ipp.isep.dei.ui.console.utils;

import pt.ipp.isep.dei.ESINF.repository.AuthenticationRepository;
import pt.ipp.isep.dei.ESINF.controller.AuthenticationController;
import pt.ipp.isep.dei.ESINF.repository.Repositories;

/**
 * Bootstrap class for initializing the application with default user roles and data.
 * Implements Runnable to execute initialization tasks on startup.
 */
public class Bootstrap implements Runnable {

    /**
     * Executes the bootstrap process, adding default users.
     * CSV data import is now available through the menu option "Load Data".
     */
    @Override
    public void run() {
        addUsers();
        // CSV data import moved to menu option - no longer auto-imported on startup
    }

    /**
     * Adds predefined user roles and users to the authentication repository.
     */
    private void addUsers() {
        //get authentication repository
        AuthenticationRepository authenticationRepository = Repositories.getInstance().getAuthenticationRepository();

        authenticationRepository.addUserRole(AuthenticationController.ROLE_PLANNER, AuthenticationController.ROLE_PLANNER);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_OPERATOR, AuthenticationController.ROLE_OPERATOR);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_DISPATCHER, AuthenticationController.ROLE_DISPATCHER);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_FREIGHT_MANAGER, AuthenticationController.ROLE_FREIGHT_MANAGER);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_ENGINEER, AuthenticationController.ROLE_ENGINEER);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_PO, AuthenticationController.ROLE_PO);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_ANALYST, AuthenticationController.ROLE_ANALYST);


        authenticationRepository.addUserWithRole("Main PLANNER", "planner1@this.app", "planner", AuthenticationController.ROLE_PLANNER);
        authenticationRepository.addUserWithRole("Side PLANNER", "planner2@this.app", "planner", AuthenticationController.ROLE_PLANNER);

        authenticationRepository.addUserWithRole("Main OPERATOR", "operator1@this.app", "operator", AuthenticationController.ROLE_OPERATOR);
        authenticationRepository.addUserWithRole("Side OPERATOR", "operator2@this.app", "operator", AuthenticationController.ROLE_OPERATOR);

        authenticationRepository.addUserWithRole("Main TRAFFIC DISPATCHER", "dispatcher1@this.app", "dispatcher", AuthenticationController.ROLE_DISPATCHER);
        authenticationRepository.addUserWithRole("Side TRAFFIC DISPATCHER", "dispatcher2@this.app", "dispatcher", AuthenticationController.ROLE_DISPATCHER);

        authenticationRepository.addUserWithRole("Main FREIGHT MANAGER", "manager1@this.app", "manager", AuthenticationController.ROLE_FREIGHT_MANAGER);
        authenticationRepository.addUserWithRole("Side FREIGHT MANAGER", "manager2@this.app", "manager", AuthenticationController.ROLE_FREIGHT_MANAGER);

        authenticationRepository.addUserWithRole("Main ENGINEER", "engineer1@this.app", "engineer", AuthenticationController.ROLE_ENGINEER);
        authenticationRepository.addUserWithRole("Side ENGINEER", "engineer2@this.app", "engineer", AuthenticationController.ROLE_ENGINEER);

        authenticationRepository.addUserWithRole("Product Owner", "po@this.app", "po", AuthenticationController.ROLE_PO);

        authenticationRepository.addUserWithRole("Analyst", "analyst1@this.app", "analyst", AuthenticationController.ROLE_ANALYST);
        authenticationRepository.addUserWithRole("Analyst", "analyst2@this.app", "analyst", AuthenticationController.ROLE_ANALYST);


    }
}
