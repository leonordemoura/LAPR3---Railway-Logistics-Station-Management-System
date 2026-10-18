package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.repository.AuthenticationRepository;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

/**
 * Controller responsible for handling user authentication and retrieving user roles.
 */

public class AuthenticationController {
    /** Constant representing the operator role. */
    public static final String ROLE_OPERATOR = "OPERATOR";
    /** Constant representing the planner role. */
    public static final String ROLE_PLANNER = "PLANNER";
    /** Constant representing the dispatcher role. */
    public static final String ROLE_DISPATCHER = "DISPATCHER";
    /** Constant representing the freight manager role. */
    public static final String ROLE_FREIGHT_MANAGER = "FREIGHT MANAGER";
    /** Constant representing the engineer role. */
    public static final String ROLE_ENGINEER = "ENGINEER";
    /** Constant representing the product owner role. */
    public static final String ROLE_PO = "PRODUCT OWNER";
    /** Constant representing the analyst role. */
    public static final String ROLE_ANALYST = "ANALYST";

    /** Repository responsible for managing authentication and user sessions. */
    private final AuthenticationRepository authenticationRepository;

    /**
     * Constructor that initializes the authentication repository.
     */
    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    /**
     * Attempts to log in a user with the provided email and password.
     *
     * @param email the user's email
     * @param pwd the user's password
     * @return true if login is successful, false otherwise
     */
    public boolean doLogin(String email, String pwd) {
        try {
            return authenticationRepository.doLogin(email, pwd);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Retrieves the list of roles assigned to the currently logged-in user.
     *
     * @return a list of user roles as DTOs, or null if no user is logged in
     */
    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }

    /**
     * Logs out the currently authenticated user.
     */
    public void doLogout() {
        authenticationRepository.doLogout();
    }
}