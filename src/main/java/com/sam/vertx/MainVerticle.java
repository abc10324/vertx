package com.sam.vertx;

import java.util.Collections;

import com.sam.vertx.controller.UserController;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(MainVerticle.class);
	
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
	
    Router router = Router.router(vertx);
    
//    router.route(HttpMethod.GET,"/User").handler(this::getUserId);
//    router.route(HttpMethod.POST,"/User/:userId").handler(this::processUserId);
    
    new UserController(router,vertx);
    
    server.requestHandler(router).listen(9000, http -> {
    	if(http.succeeded()) {
    		startPromise.complete();
    		log.info("HTTP server started on port 9000");
    	} else {
    		startPromise.fail(http.cause());
    	}
    });
    
  }
  
  	private void processUserId(RoutingContext ctx) {
  		String userId = ctx.pathParam("userId");
  		
    	JsonObject jsonObj = JsonObject.mapFrom(Collections.singletonMap("userId", userId));
    	
    	ctx.response()
    	   .putHeader("content-type", "application/json")
    	   .end(jsonObj.toString());
  	}
  	
  	private void getUserId(RoutingContext ctx) {
  		JsonObject jsonObj = JsonObject.mapFrom(Collections.singletonMap("userId", "newUserId"));
    	
    	ctx.response()
    	   .putHeader("content-type", "application/json")
    	   .end(jsonObj.toString());
  	}
}
