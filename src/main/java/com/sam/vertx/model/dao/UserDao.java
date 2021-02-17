package com.sam.vertx.model.dao;

import java.util.Collections;

import com.sam.vertx.model.User;
import com.sam.vertx.util.Const;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;

public class UserDao {

	private EventBus eventBus;
	
	private MySQLPool client;
	
	private static Logger log = LoggerFactory.getLogger(UserDao.class);
	
	private static final String INSERT_STAT = "INSERT INTO user(userId,name) VALUES (?,?)";

	private static final String FIND_BY_ID1 = "SELECT * FROM user WHERE userId=#{userId}";
	
	public UserDao(Vertx vertx) {
		this.eventBus = vertx.eventBus();
		init(vertx);
	}
	
	private void init(Vertx vertx) {
		setDataBaseConnection(vertx);
		
		eventBus.consumer("user.dao.addUser", this::addUser);
		eventBus.consumer("user.dao.getUser", this::getUser);
	}
	
	private void setDataBaseConnection(Vertx vertx) {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions()
				  .setPort(Const.DB_PORT)
				  .setHost(Const.DB_HOST)
				  .setDatabase(Const.DB_NAME)
				  .setUser(Const.DB_USER)
				  .setPassword(Const.DB_PWD);

		// Pool options
		PoolOptions poolOptions = new PoolOptions()
				.setMaxSize(5);

		// Create the client pool
		this.client = MySQLPool.pool(vertx,connectOptions, poolOptions);
	}
	
	public void addUser(Message<Object> request) {
		User newUser = (User) request.body();
		
		
		client.preparedQuery(INSERT_STAT)
		      .execute(Tuple.of(newUser.getUserId(),newUser.getName()), ar -> {
		    	  if(ar.succeeded()) {
		    		  log.info("Insert user " + newUser.getUserId() + " success");
		    		  request.reply("success");
		    	  } else {
		    		  log.debug("Insert failure");
		    		  log.debug(ar.cause().getMessage());
		    		  request.fail(500, "insert fail");
		    	  }
		      });
	}
	
	public void getUser(Message<Object> request) {
		String userId = (String) request.body();
		
		SqlTemplate.forQuery(client, FIND_BY_ID1)
				.mapTo(User.class)
				.execute(Collections.singletonMap("userId", userId), ar -> {
					if(ar.succeeded() && ar.result().iterator().hasNext()) {
						User result = ar.result().iterator().next();
						request.reply(result, Const.USER_CODEC);
					}else{
						request.fail(500, null);
					}
				});
		
	}
	
}
