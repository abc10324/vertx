package com.sam.vertx.model.service;

import com.sam.vertx.util.Const;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class UserService {
	
	private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	private EventBus eventBus;
	
	public UserService(Vertx vertx) {
		this.eventBus = vertx.eventBus();
		init();
	}
	
	private void init() {
		eventBus.consumer("user.svc.addUser",this::addUser);
		eventBus.consumer("user.svc.getUser",this::getUser);
	}
	
	public void addUser(Message<Object> request) {
		eventBus.request("user.dao.addUser", 
						 request.body(), 
						 Const.USER_CODEC, 
	        ar -> {
	        	String result = ar.succeeded() ? "success" : "failure";
	        	request.reply(result);
	        }
        );
	}
	
	public void getUser(Message<Object> request) {
		log.info("start get user");
		eventBus.request("user.dao.getUser", 
						 request.body(), 
			ar -> {
				if(ar.succeeded()) {
					request.reply(ar.result().body(),Const.USER_CODEC);
				} else {
					request.fail(404, "not found");
				}
				
			}
		);
	}
	
}
