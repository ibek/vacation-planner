package link.bek.poc.api;

import java.util.List;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import link.bek.poc.dao.VacationManager;

public class VacationAPI {

    private VacationManager vacationManager;
    
    public VacationAPI(VacationManager vacationManager) {
        this.vacationManager = vacationManager;
    }
    
    public void create(RoutingContext context) {
        JsonObject vacation = context.getBodyAsJson();
        vacationManager.newVacation(vacation, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            vacationManager.transformDates(vacation);
            context.response().end(vacation.toString());
        });
    }
    
    public void retrieveById(RoutingContext context) {
        HttpServerRequest request = context.request();
        String vacationId = request.getParam("vacationId");
        if (vacationId == null) {
            context.fail(400);
            return;
        }
        vacationManager.vacationById(vacationId, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            JsonObject vacation = result.result();
            vacation = vacationManager.transformDates(vacation);
            context.response().end(vacation.encode());
        });
    }
    
    public void retrieveByEmployee(RoutingContext context) {
        HttpServerRequest request = context.request();
        String employee = request.getParam("employee");
        if (employee == null) {
            context.fail(400);
            return;
        }
        vacationManager.vacationByEmployee(employee, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            List<JsonObject> vacations = result.result();
            for (JsonObject vacation : vacations) {
                vacationManager.transformDates(vacation);
            }
            JsonArray response = new JsonArray((List)vacations);
            context.response().end(response.encode());
        });
    }
    
    public void retrieveByDuration(RoutingContext context) {
        HttpServerRequest request = context.request();
        String from = request.getParam("from");
        String to = request.getParam("to");
        if (from == null || to == null) {
            context.fail(400);
            return;
        }
        vacationManager.vacationByDuration(from, to, result -> {
            if (result.failed()) {
                context.fail(result.cause());
                return;
            }
            JsonArray response = new JsonArray((List)result.result());
            context.response().end(response.encode());
        });
    }
    
}
