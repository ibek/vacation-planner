package link.bek.poc.dao;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class VacationManager {
    
    private static final String COLLECTION = "vacations";
    private MongoClient mongo;

    public VacationManager(MongoClient mongo) {
        this.mongo = mongo;
    }

    public void newVacation(JsonObject vacation, Handler<AsyncResult<String>> handler) {
        boolean verified = checkVacation(vacation);
        if (!verified) {
            handler.handle(Future.failedFuture("Vacation object is incorrect: \n" + vacation.encodePrettily()));
        }
        mongo.insert(COLLECTION, vacation, handler);
    }
    
    public void vacationById(String vacationId, Handler<AsyncResult<JsonObject>> handler) {
        JsonObject query = new JsonObject();
        query.put("_id", vacationId);
        mongo.findOne(COLLECTION, query, null, handler);
    }
    
    public void vacationByEmployee(String employee, Handler<AsyncResult<List<JsonObject>>> handler) {
        JsonObject query = new JsonObject();
        query.put("employee", employee);
        mongo.find(COLLECTION, query, handler);
    }
    
    public void vacationByDuration(String from, String to, Handler<AsyncResult<List<JsonObject>>> handler) {
        JsonObject query = new JsonObject();
        query.put("from", "{ $gte:" + from + "}");
        query.put("to", "{ $lte:" + to + "}");
        mongo.find(COLLECTION, query, handler);
    }
    
    private boolean checkVacation(JsonObject vacation) {
        String[] elements = {"employee", "description", "from", "to", "approver", "status"};
        if (vacation.size() != elements.length) {
            return false;
        }
        for (String elm : elements) {
            if (!vacation.containsKey(elm)) {
                return false;
            }
        }
        return true;
    }
}
