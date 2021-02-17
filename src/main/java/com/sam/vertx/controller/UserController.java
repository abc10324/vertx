package com.sam.vertx.controller;

import java.util.Collections;

import com.sam.vertx.model.User;
import com.sam.vertx.model.service.UserService;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UserController {
	
	private static final String ROOT_MAPPING = "/User";
	
	private Router router;
	
	private UserService userSvc;
	
	public UserController(Router router,Vertx vertx) {
		
		this.router = router;
		this.userSvc = new UserService(vertx);
		init();
	}
	
	private void init() {
		router.route(HttpMethod.GET, ROOT_MAPPING).handler(this::getNewUser);
		router.route(HttpMethod.POST, ROOT_MAPPING + "/:userId/:name").handler(this::addUser);
	}
	
	private void addUser(RoutingContext ctx) {
		String userId = ctx.pathParam("userId");
		String name = ctx.pathParam("name");
  		
		User user = new User();
		user.setUserId(userId);
		user.setName(name);
		
		userSvc.addUser(user);
		
    	JsonObject jsonObj = JsonObject.mapFrom(Collections.singletonMap("status", "success"));
    	
    	ctx.response()
    	   .putHeader("content-type", "application/json")
    	   .end(jsonObj.toString());
	}
	
	private void getNewUser(RoutingContext ctx) {
		String userId = ctx.request().getParam("userId");
		
		
		userSvc.getUser(userId).onComplete(ar -> {
			
			JsonObject jsonObj = ar.succeeded() 
							   ? JsonObject.mapFrom(ar.result()) 
			    			   : JsonObject.mapFrom(Collections.singletonMap("status", "failed"));
			
			ctx.response()
	    	   .putHeader("content-type", "application/json")
	    	   .end(jsonObj.toString());
		});
    	
	}
	
}
