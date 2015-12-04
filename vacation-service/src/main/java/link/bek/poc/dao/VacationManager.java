package link.bek.poc.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    
    public JsonObject transformDates(JsonObject vacation) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long from = vacation.getLong("from");
        vacation.put("from", dateFormat.format(new Date(from)));
        Long to = vacation.getLong("to");
        vacation.put("to", dateFormat.format(new Date(to)));
        return vacation;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String from = vacation.getString("from");
        try {
			Date fromDate = dateFormat.parse(from);
			vacation.put("from", fromDate.getTime());
		} catch (ParseException e) {
			return false;
		}
        
        String to = vacation.getString("to");
        try {
			Date toDate = dateFormat.parse(to);
			vacation.put("to", toDate.getTime());
		} catch (ParseException e) {
			return false;
		}
        return true;
    }
}
