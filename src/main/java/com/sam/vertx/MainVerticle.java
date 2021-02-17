package com.sam.vertx;

import com.sam.vertx.controller.UserController;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(MainVerticle.class);
	
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
	
    Router router = Router.router(vertx);
    
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
  
}
