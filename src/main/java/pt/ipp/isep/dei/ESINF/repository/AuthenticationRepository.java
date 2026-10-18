package pt.ipp.isep.dei.ESINF.repository;

import pt.isep.lei.esoft.auth.AuthFacade;
import pt.isep.lei.esoft.auth.UserSession;

/**
 * Repository class for handling authentication-related operations.
 * Provides methods for user login, logout, session management, and role-based user management.
 */
public class AuthenticationRepository {
    private final AuthFacade authenticationFacade;

    /**
     * Constructs an AuthenticationRepository instance.
     * Initializes the authentication facade.
     */
    public AuthenticationRepository() {
        authenticationFacade = new AuthFacade();
    }

    /**
     * Performs a login attempt with the specified email and password.
     *
     * @param email the user's email
     * @param pwd the user's password
     * @return true if the login is successful, false otherwise
     */
    public boolean doLogin(String email, String pwd) {
        return authenticationFacade.doLogin(email, pwd).isLoggedIn();
    }

    /**
     * Logs out the current user.
     */
    public void doLogout() {
        authenticationFacade.doLogout();
    }

    /**
     * Retrieves the current user's session.
     *
     * @return the current UserSession, or null if no user is logged in
     */
    public UserSession getCurrentUserSession() {
        return authenticationFacade.getCurrentUserSession();
    }

    /**
     * Adds a new user role with the specified ID and description.
     *
     * @param id the role ID
     * @param description the role description
     * @return true if the role was added successfully, false otherwise
     */
    public boolean addUserRole(String id, String description) {
        return authenticationFacade.addUserRole(id, description);
    }

    /**
     * Adds a new user with the specified name, email, password, and role.
     *
     * @param name the user's name
     * @param email the user's email
     * @param pwd the user's password
     * @param roleId the ID of the role to assign
     * @return true if the user was added successfully, false otherwise
     */
    public boolean addUserWithRole(String name, String email, String pwd, String roleId) {
        return authenticationFacade.addUserWithRole(name, email, pwd, roleId);
    }
}