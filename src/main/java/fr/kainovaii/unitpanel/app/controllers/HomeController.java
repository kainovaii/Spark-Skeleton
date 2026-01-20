package fr.kainovaii.unitpanel.app.controllers;

import fr.kainovaii.core.web.methods.GET;
import fr.kainovaii.core.web.controller.BaseController;
import fr.kainovaii.core.web.controller.Controller;
import spark.Request;
import spark.Response;

import java.util.Map;

@Controller
public class HomeController extends BaseController
{
    @GET(value = "/", name = "site_home")
    private Object homepage(Request req, Response res)
    {
        return render("home.html", Map.of());
    }
}