package com.obsidian.app.http.controllers;

import com.obsidian.core.http.controller.BaseController;
import com.obsidian.core.http.controller.annotations.Controller;
import com.obsidian.core.routing.methods.GET;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Base class for all controllers in the application.
 * Every controller should extend this class instead of ObsidianController directly,
 * so that shared behavior can be added here later without touching every controller.
 */
@Controller
public class WelcomeController extends BaseController
{
    @GET(value = "/", name = "site.home")
    private Object homepage(Request req, Response res)
    {
        return render("welcome.html", Map.of());
    }
}