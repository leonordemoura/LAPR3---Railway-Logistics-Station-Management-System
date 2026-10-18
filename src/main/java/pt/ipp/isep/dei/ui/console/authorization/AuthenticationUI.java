package pt.ipp.isep.dei.ui.console.authorization;

import pt.ipp.isep.dei.ESINF.controller.AuthenticationController;
//import pt.ipp.isep.dei.ui.console.menu.PlayerUI;
import pt.ipp.isep.dei.ui.console.menu.*;
import pt.ipp.isep.dei.ui.console.menu.EngineerUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * User interface for handling user authentication and role selection.
 * Implements Runnable to execute the login process and redirect to the appropriate role-based UI.
 */
public class AuthenticationUI implements Runnable {
    private final AuthenticationController ctrl;

    /**
     * Constructor of AuthenticationUI that initiates the controller.
     */
    public AuthenticationUI() {
        ctrl = new AuthenticationController();
    }
    /**
     * Executes the authentication process, including login, role selection, and UI redirection.
     */
    @Override
    public void run() {
        boolean success = doLogin();

        if (success) {
            List<UserRoleDTO> roles = this.ctrl.getUserRoles();
            if ((roles == null) || (roles.isEmpty())) {
                System.out.println("No role assigned to user.");
            } else {
                UserRoleDTO role = selectsRole(roles);
                if (!Objects.isNull(role)) {
                    List<MenuItem> rolesUI = getMenuItemForRoles();
                    this.redirectToRoleUI(rolesUI, role);
                } else {
                    System.out.println("No role selected.");
                }
            }
        }
        this.logout();
    }

    /**
     * Creates a list of menu items corresponding to available user roles.
     *
     * @return a list of MenuItem objects for role-based UIs
     */
    private List<MenuItem> getMenuItemForRoles() {
        List<MenuItem> rolesUI = new ArrayList<>();
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_PLANNER, new PlannerUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_OPERATOR, new OperatorUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_DISPATCHER, new DispatcherUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_FREIGHT_MANAGER, new FreightManagerUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_ENGINEER, new EngineerUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_PO, new AdminUI()));
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_ANALYST, new AnalystUI()));



        //TODO: Complete with other user roles and related RoleUI
        return rolesUI;
    }

    /**
     * Performs the login process with a maximum of three attempts.
     *
     * @return true if login is successful, false otherwise
     */
    private boolean doLogin() {
        System.out.println("\n\n--- LOGIN UI ---------------------------");

        int maxAttempts = 3;
        boolean success = false;
        do {
            maxAttempts--;
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            String pwd = Utils.readLineFromConsole("Enter Password: ");

            success = ctrl.doLogin(id, pwd);
            if (!success) {
                System.out.println("Invalid UserId and/or Password. \n You have  " + maxAttempts + " more attempt(s).");
            }


        } while (!success && maxAttempts > 0);
        return success;
    }

    /**
     * Logs out the current user.
     */
    private void logout() {
        ctrl.doLogout();
    }

    /**
     * Redirects to the appropriate role-based UI based on the selected role.
     *
     * @param rolesUI the list of available role-based menu items
     * @param role the selected user role
     */
    private void redirectToRoleUI(List<MenuItem> rolesUI, UserRoleDTO role) {
        boolean found = false;
        Iterator<MenuItem> it = rolesUI.iterator();
        while (it.hasNext() && !found) {
            MenuItem item = it.next();
            found = item.hasDescription(role.getDescription());
            if (found) {
                item.run();
            }
        }
        if (!found) {
            System.out.println("There is no UI for users with role '" + role.getDescription() + "'");
        }
    }

    /**
     * Allows the user to select a role from the provided list of roles.
     *
     * @param roles the list of available user roles
     * @return the selected UserRoleDTO, or null if no role is selected
     */
    private UserRoleDTO selectsRole(List<UserRoleDTO> roles) {
        if (roles.size() == 1) {
            return roles.get(0);
        } else {
            return (UserRoleDTO) Utils.showAndSelectOne(roles, "Select the role you want to adopt in this session:");
        }
    }
}