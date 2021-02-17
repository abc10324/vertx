package com.sam.vertx.util;

import io.vertx.core.eventbus.DeliveryOptions;

public class Const {
	
	public static final String DB_HOST = "localhost";
	public static final Integer DB_PORT = 3306;
	public static final String DB_NAME = "db01";
	public static final String DB_USER = "root";
	public static final String DB_PWD = "passw0rd";
	
	public static final DeliveryOptions USER_CODEC = new DeliveryOptions().setCodecName("UserCodec");
	
}
