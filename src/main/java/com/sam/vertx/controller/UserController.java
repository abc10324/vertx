package com.sam.vertx.controller;

import java.util.Collections;

import com.sam.vertx.model.User;
import com.sam.vertx.util.Const;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UserController implements Routing {
	
	private static Logger log = LoggerFactory.getLogger(UserController.class);
	
	private static final String ROOT_MAPPING = "/User";
	
	private Router router;
	
	private EventBus eventBus;
	
	public UserController(Vertx vertx) {
		this.router = Router.router(vertx);
		this.eventBus = vertx.eventBus();
		initRouting();
	}
	
	private void initRouting() {
		router.route(HttpMethod.GET, ROOT_MAPPING).handler(this::getUser);
		router.route(HttpMethod.POST, ROOT_MAPPING + "/:userId/:name").handler(this::addUser);
	}
	
	private void addUser(RoutingContext ctx) {
		String userId = ctx.pathParam("userId");
		String name = ctx.pathParam("name");
  		
		User user = new User();
		user.setUserId(userId);
		user.setName(name);
		
		eventBus.request("user.svc.addUser", 
				         user, 
				         Const.USER_CODEC, 
	         ar -> {
	        	 JsonObject jsonObj = ar.succeeded() ? 
	        			              JsonObject.mapFrom(Collections.singletonMap("status", "success")) :
        			            	  JsonObject.mapFrom(Collections.singletonMap("status", "failure"));
	        	 sendResponse(ctx, jsonObj);
	         }
		);
		
	}
	
	private void getUser(RoutingContext ctx) {
		String userId = ctx.request().getParam("userId");
		
		eventBus.request("user.svc.getUser", 
				         userId,
	        ar -> {
	        	
	        	JsonObject jsonObj = ar.succeeded() 
							   ? JsonObject.mapFrom((User) ar.result().body()) 
			    			   : JsonObject.mapFrom(Collections.singletonMap("status", "failed"));
				
			    sendResponse(ctx, jsonObj);
			}
	    );
	}
	
	private void sendResponse(RoutingContext ctx, JsonObject jsonObj) {
		ctx.response()
	 	   .putHeader("content-type", "application/json")
	 	   .end(jsonObj.toString());
	}

	@Override
	public Router getRouter() {
		return this.router;
	}
	
}
