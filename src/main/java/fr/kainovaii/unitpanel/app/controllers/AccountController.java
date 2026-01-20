package fr.kainovaii.unitpanel.app.controllers;

import fr.kainovaii.core.security.HasRole;
import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.core.web.methods.POST;
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
    @GET(value = "/users/account", name = "user/account")
    private Object settings(Request req, Response res)
    {
        Long userId = getLoggedUser(req).getLongId();
        return render("account/settings.html", Map.of());
    }

    @HasRole("DEFAULT")
    @POST("/users/account")
    private Object updateUser(Request req, Response res)
    {
        Session session = req.session(true);
        String newUsername = req.queryParams("username");
        String newPassword = req.queryParams("password");
        String currentUsername = getLoggedUser(req).getUsername();
        try {
            User user = DB.withConnection(() -> userRepository.findByUsername((currentUsername)));
            final String finalUsername = (newUsername == null || newUsername.isEmpty()) ? user.getUsername() : newUsername;
            final String finalPassword = (newPassword == null || newPassword.isEmpty()) ? user.getPassword() : BCrypt.hashpw(newPassword, BCrypt.gensalt());
            boolean updateUser = DB.withConnection(() -> userRepository.updateByUsername(currentUsername, finalUsername, finalPassword));
            if (updateUser) {
                session.attribute("username", finalUsername);
                setFlash(req, "success", "Update success");
            } else {setFlash(req, "error", "Update error");}

            res.redirect("/users/my-account");
            return null;
        } catch (RuntimeException exception) {
            return redirectWithFlash(req,  res, "error", exception.getMessage(), "/users/my-account");
        }
    }
}
