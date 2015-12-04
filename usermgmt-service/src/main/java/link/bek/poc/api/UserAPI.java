package link.bek.poc.api;

import java.util.List;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import link.bek.poc.dao.UserManager;

public class UserAPI {

    private UserManager userManager;
    
    public UserAPI(UserManager userManager) {
        this.userManager = userManager;
    }
    
    public void create(RoutingContext context) {
        JsonObject user = context.getBodyAsJson();
        userManager.newUser(user, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            context.response().end(user.toString());
        });
    }
    
    public void retrieveById(RoutingContext context) {
        HttpServerRequest request = context.request();
        String userId = request.getParam("userId");
        if (userId == null) {
            context.fail(400);
            return;
        }
        userManager.userById(userId, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            JsonObject user = result.result();
            context.response().end(user.encode());
        });
    }
    
    public void retrieveByManager(RoutingContext context) {
        HttpServerRequest request = context.request();
        String manager = request.getParam("manager");
        if (manager == null) {
            context.fail(400);
            return;
        }
        userManager.usersByManager(manager, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            List<JsonObject> users = result.result();
            JsonArray response = new JsonArray((List)users);
            context.response().end(response.encode());
        });
    }
    
}
