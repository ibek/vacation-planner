package link.bek.poc.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long fromDate = null;
        Long toDate = null;
        try {
            fromDate = dateFormat.parse(from).getTime();
            toDate = dateFormat.parse(to).getTime();
        } catch (ParseException ex) {
            handler.handle(Future.failedFuture(ex));
        }
        List<JsonObject> orDuration = new ArrayList<JsonObject>();
        orDuration.add(new JsonObject().put("from", new JsonObject().put("$lte", toDate)).put("to", new JsonObject().put("$gte", toDate)));
        orDuration.add(new JsonObject().put("from", new JsonObject().put("$lte", fromDate)).put("to", new JsonObject().put("$gte", fromDate)));
        query.put("$or", new JsonArray(orDuration));
        mongo.find(COLLECTION, query, handler);
    }

    public void updateVacation(JsonObject vacation, Handler<AsyncResult<Void>> handler) {
        JsonObject query = new JsonObject().put("_id", vacation.getString("_id"));
        mongo.replace(COLLECTION, query, vacation, handler);
    }
    
    private boolean checkVacation(JsonObject vacation) {
        String[] elements = {"employee", "description", "from", "to", "approver", "status"};
        if (vacation.containsKey("_id")) {
            vacation.remove("_id");
        }
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
