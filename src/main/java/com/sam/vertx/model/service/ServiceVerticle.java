package com.sam.vertx.model.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class ServiceVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(ServiceVerticle.class);
	
	@Override
	public void start() throws Exception {
		log.info("Starting service verticle");
		initService();
		log.info("Finish service verticle starting");
	}
	
	private void initService() {
		new UserService(vertx);
	}
	
}
