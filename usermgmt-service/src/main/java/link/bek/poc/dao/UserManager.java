package link.bek.poc.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class UserManager {
    
    private static final String COLLECTION = "users";
    private MongoClient mongo;

    public UserManager(MongoClient mongo) {
        this.mongo = mongo;
    }

    public void newUser(JsonObject user, Handler<AsyncResult<String>> handler) {
        boolean verified = checkUser(user);
        if (!verified) {
            handler.handle(Future.failedFuture("User object is incorrect: \n" + user.encodePrettily()));
        }
        mongo.insert(COLLECTION, user, handler);
    }
    
    public void userById(String userId, Handler<AsyncResult<JsonObject>> handler) {
        JsonObject query = new JsonObject();
        query.put("userId", userId);
        mongo.findOne(COLLECTION, query, null, handler);
    }
    
    public void usersByManager(String managerId, Handler<AsyncResult<List<JsonObject>>> handler) {
        JsonObject query = new JsonObject();
        query.put("manager", managerId);
        mongo.find(COLLECTION, query, handler);
    }
    
    private boolean checkUser(JsonObject user) {
        String[] elements = {"userId", "name", "manager"};
        if (user.size() != elements.length) {
            return false;
        }
        for (String elm : elements) {
            if (!user.containsKey(elm)) {
                return false;
            }
        }
        return true;
    }
}
