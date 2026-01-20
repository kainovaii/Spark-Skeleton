package fr.kainovaii.unitpanel.app.controllers;

import fr.kainovaii.core.security.HasRole;
import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.core.web.methods.POST;
import fr.kainovaii.core.web.route.Route;
import fr.kainovaii.unitpanel.app.models.User;
import fr.kainovaii.unitpanel.app.repository.UserRepository;
import fr.kainovaii.core.database.DB;
import fr.kainovaii.core.web.controller.BaseController;
import fr.kainovaii.core.web.controller.Controller;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Session;
import java.util.Map;

@Controller
public class AccountController extends BaseController
{
    private final UserRepository userRepository = new UserRepository();

    @HasRole("DEFAULT")
    @GET(value = "/users/account", name = "user_account")
    private Object settings(Request req, Response res)
    {
        return render("account/settings.html", Map.of());
    }

    @HasRole("DEFAULT")
    @POST("/users/account")
    private Object updateUser(Request req, Response res)
    {
        Session session = req.session(true);
        String newUsername = req.queryParams("username");
        String newPassword = req.queryParams("password");
        String newEmail = req.queryParams("email");
        String currentUsername = getLoggedUser(req).getUsername();
        try {
            User user = DB.withConnection(() -> userRepository.findByUsername((currentUsername)));
            final String finalUsername = (newUsername == null || newUsername.isEmpty()) ? user.getUsername() : newUsername;
            final String finalEmail = (newEmail == null || newEmail.isEmpty()) ? user.getEmail() : newEmail;
            final String finalPassword = (newPassword == null || newPassword.isEmpty()) ? user.getPassword() : BCrypt.hashpw(newPassword, BCrypt.gensalt());
            boolean updateUser = DB.withConnection(() -> userRepository.loggUserUpdate(currentUsername, finalUsername, finalEmail, finalPassword));

            if (updateUser) {
                return redirectWithFlash(req,  res, "success","Update success", Route.getPath("user_account"));
            } else {
                return redirectWithFlash(req,  res, "error", "Update error", Route.getPath("user_account"));
            }
        } catch (RuntimeException exception) {
            return redirectWithFlash(req,  res, "error", exception.getMessage(), Route.getPath("user_account"));
        }
    }
}
