package com.sam.vertx.model.dao;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class DaoVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(DaoVerticle.class);
	
	@Override
	public void start() throws Exception {
		log.info("Starting dao verticle");
		initDao();
		log.info("Finish dao verticle starting");
	}
	
	private void initDao() {
		new UserDao(vertx);
	}
	
}
