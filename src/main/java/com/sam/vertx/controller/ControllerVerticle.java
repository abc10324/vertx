package com.sam.vertx.controller;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class ControllerVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(ControllerVerticle.class);
	
	private Router router = Router.router(vertx);
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		initController();
		initServer(startPromise);
	}
	
	private void initController() {
		List<Routing> subRouterList = new ArrayList<>();
		
		subRouterList.add(new UserController(vertx));
		
		for(Routing subRouter : subRouterList) {
			router.mountSubRouter("/", subRouter.getRouter());
		}
	}
	
	private void initServer(Promise<Void> startPromise) {
		vertx.createHttpServer().requestHandler(router).listen(9000, http -> {
	    	if(http.succeeded()) {
	    		startPromise.complete();
	    		log.info("HTTP server started on port 9000");
	    	} else {
	    		startPromise.fail(http.cause());
	    	}
	    });
	}
	
}
