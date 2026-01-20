package fr.kainovaii.unitpanel.app.controllers;

import fr.kainovaii.core.security.HasRole;
import fr.kainovaii.core.web.route.Route;
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

import static spark.Spark.get;
import static spark.Spark.post;

@Controller
public class LoginController extends BaseController
{
    private final UserRepository userRepository = new UserRepository();

    @GET(value = "/users/login", name = "user_login")
    private Object loginFront(Request req, Response res)
    {
        if (!isLogged(req)) {
            return render("account/login.html", Map.of());
        } else {
            redirectWithFlash(req,  res, "error", "Your are already logged in", "/");
        }
        return null;
    }

    @POST("/users/login")
    private Object loginBack(Request req, Response res)
    {
        String usernameParam = req.queryParams("username");
        String passwordParam = req.queryParams("password");
        Session session = req.session(true);
        try {
            return DB.withConnection(() ->
            {
                if (!UserRepository.userExist(usernameParam)) redirectWithFlash(req,  res, "error", "User not found", Route.getPath("user_login"));

                User user = userRepository.findByUsername(usernameParam);

                if (BCrypt.checkpw(passwordParam, user.getPassword()))
                {
                    session.attribute("logged", true);
                    session.attribute("id", user.getId());
                    res.redirect(Route.getPath("site_home"));
                    return true;
                }
                return redirectWithFlash(req,  res, "error", "Incorect login", Route.getPath("user_login"));
            });
        } catch (RuntimeException exception) {
            return redirectWithFlash(req,  res, "error", exception.getMessage(), Route.getPath("user_login"));
        }
    }

    @HasRole("DEFAULT")
    @GET(value = "/users/logout", name = "user_logout")
    private Object logout(Request req, Response res)
    {
        Session session = req.session(true);
        if (isLogged(req)) { session.invalidate(); }
        return redirectWithFlash(req,  res, "success", "Success logout", Route.getPath("user_login"));
    }
}
