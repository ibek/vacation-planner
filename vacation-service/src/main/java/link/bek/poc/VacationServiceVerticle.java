package link.bek.poc;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import link.bek.poc.api.VacationAPI;
import link.bek.poc.dao.VacationManager;

public class VacationServiceVerticle extends AbstractVerticle {
    
    private HttpServer server;
    private VacationManager vacationManager;
    private VacationAPI vacationAPI;

    @Override
    public void start(Future<Void> future) {
        JsonObject config = config();
        int port = config.getInteger("http.port", 8080);
        String mongoHost = System.getProperty("mongo.host");
        if (mongoHost == null) {
            mongoHost = "0.0.0.0";
        }
        String mongoPort = System.getProperty("mongo.port");
        if (mongoPort == null) {
            mongoPort = "27017";
        }
        vacationManager = new VacationManager(MongoClient.createShared(vertx, mongoConfig(mongoHost, Integer.valueOf(mongoPort))));
        vacationAPI = new VacationAPI(vacationManager);
        server = vertx.createHttpServer(createOptions(port));
        server.requestHandler(createRouter()::accept);
        server.listen(result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> future) {
        if (server == null) {
            future.complete();
            return;
        }
        server.close(result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                future.complete();
            }
        });
    }
    
    private static HttpServerOptions createOptions(int port) {
        HttpServerOptions options = new HttpServerOptions();
        options.setHost("0.0.0.0");
        options.setPort(port);
        return options;
    }

    private Router createRouter() {
        Router router = Router.router(vertx);
        router.route().failureHandler(ErrorHandler.create(true));

        /* API */
        router.mountSubRouter("/api", apiRouter());

        return router;
    }
    
    private Router apiRouter() {
        Router router = Router.router(vertx);
        router.route().consumes("application/json");
        router.route().produces("application/json");
        router.route().handler(BodyHandler.create());
        router.route().handler(context -> {
            context.response().headers().add(CONTENT_TYPE, "application/json");
            context.response().headers().add("Access-Control-Allow-Origin", "*");
            context.next();
        });

        /* API to deal with feeds : token required */
        router.post("/vacations").handler(vacationAPI::create);
        router.post("/vacations/:vacationId").handler(vacationAPI::updateStatus);
        router.get("/vacations/:vacationId").handler(vacationAPI::retrieveById);
        router.get("/employees/:employee/vacations").handler(vacationAPI::retrieveByEmployee);
        router.get("/vacations").handler(vacationAPI::retrieveByDuration);

        return router;
    }
    
    private static JsonObject mongoConfig(String mongoHost, int mongoPort) {
        JsonObject config = new JsonObject();
        config.put("host", mongoHost);
        config.put("port", mongoPort);
        config.put("db_name", "vertx-feeds");
        return config;
    }
    
}
