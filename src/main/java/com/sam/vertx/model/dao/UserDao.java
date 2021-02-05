package com.sam.vertx.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sam.vertx.model.User;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

public class UserDao {

	private Vertx vertx;
	
	private MySQLPool client;
	
	private static Logger log = LoggerFactory.getLogger(UserDao.class);
	
	private static final String INSERT_STAT = "INSERT INTO user(userId,name) VALUES (?,?)";

	private static final String FIND_BY_ID = "SELECT * FROM user WHERE userId=?";
	
	public UserDao(Vertx vertx) {
		this.vertx = vertx;
		init();
	}
	
	private void init() {
		MySQLConnectOptions connectOptions = new MySQLConnectOptions()
				  .setPort(3306)
				  .setHost("localhost")
				  .setDatabase("db01")
				  .setUser("root")
				  .setPassword("passw0rd");

		// Pool options
		PoolOptions poolOptions = new PoolOptions()
				.setMaxSize(5);

		// Create the client pool
		this.client = MySQLPool.pool(this.vertx,connectOptions, poolOptions);

	}
	
	public void addUser(User newUser) {
		client.preparedQuery(INSERT_STAT)
		      .execute(Tuple.of(newUser.getUserId(),newUser.getName()), ar -> {
		    	  if(ar.succeeded()) {
		    		  log.info("Insert user " + newUser.getUserId() + " success");
		    	  } else {
		    		  log.debug("Insert failure");
		    		  log.debug(ar.cause().getMessage());
		    	  }
		      });
	}
	
	public Optional<User> getUser(String userId) {
		List<User> resultList = new ArrayList<>();
		
		client.preparedQuery(FIND_BY_ID)
		      .execute(Tuple.of(userId), ar -> {
		    	  if(ar.succeeded()) {
		    		  ar.result().forEach(rowData -> {
		    			  User user = new User();
		    			  user.setUserId(rowData.getString("userId"));
		    			  user.setName(rowData.getString("name"));
		    			  
		    			  resultList.add(user);
		    			  
		    			  log.info("Find " + user.getName());
		    		  });

		    		  log.info("find " + ar.result().size() + " users");
		    	  } else {
		    		  log.info("Nothing find");
		    	  }
		      });
		log.info("Finish query");
		log.info("Result : " + resultList.size() + " users");
		
		return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
	}
	
}
