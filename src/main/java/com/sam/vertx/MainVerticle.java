package com.sam.vertx;

import com.sam.vertx.model.User;
import com.sam.vertx.model.codec.GenericCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(MainVerticle.class);
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		log.info("Starting main verticle");
		initVerticles();
		registEventBusCodec();
		log.info("Finish main verticle starting");
		
		startPromise.complete();
	}
  
  
	private void initVerticles() {
		vertx.deployVerticle("com.sam.vertx.model.service.ServiceVerticle");  
		vertx.deployVerticle("com.sam.vertx.model.dao.DaoVerticle");
		vertx.deployVerticle("com.sam.vertx.controller.ControllerVerticle");
	}
  
	private void registEventBusCodec() {
		vertx.eventBus().registerCodec(new GenericCodec<User>(User.class));
	}
  
}
