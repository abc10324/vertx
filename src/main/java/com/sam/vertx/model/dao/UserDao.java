package com.sam.vertx.model.dao;

import java.util.Collections;

import com.sam.vertx.model.User;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;

public class UserDao {

	private Vertx vertx;
	
	private MySQLPool client;
	
	private static Logger log = LoggerFactory.getLogger(UserDao.class);
	
	private static final String INSERT_STAT = "INSERT INTO user(userId,name) VALUES (?,?)";

	private static final String FIND_BY_ID = "SELECT * FROM user WHERE userId=?";
	private static final String FIND_BY_ID1 = "SELECT * FROM user WHERE userId=#{userId}";
	
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
	
	public Future<User> getUser(String userId) {
//		List<User> resultList = new ArrayList<>();
		
//		client.preparedQuery(FIND_BY_ID)
//		      .execute(Tuple.of(userId), ar -> {
//		    	  if(ar.succeeded()) {
//		    		  ar.result().forEach(rowData -> {
//		    			  User user = new User();
//		    			  user.setUserId(rowData.getString("userId"));
//		    			  user.setName(rowData.getString("name"));
//		    			  
//		    			  resultList.add(user);
//		    			  
//		    			  log.info("Find " + user.getName());
//		    		  });
//
//		    		  log.info("find " + ar.result().size() + " users");
//		    	  } else {
//		    		  log.info("Nothing find");
//		    	  }
//		      });

//		log.info("Finish query");
//		log.info("Result : " + resultList.size() + " users");
		
//		return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
		return SqlTemplate.forQuery(client, FIND_BY_ID1)
						.mapTo(User.class)
						.execute(Collections.singletonMap("userId", userId))
						.map(rowSet -> rowSet.iterator().next());
						
//		return client.preparedQuery(FIND_BY_ID).execute(Tuple.of(userId)).map(result -> {
//			User user = new User();
//			result.forEach(rowData -> {
//				user.setId(rowData.getInteger("id"));
//				user.setUserId(rowData.getString("userId"));
//				user.setName(rowData.getString("name"));
//			});	
//				
//			return user;
//		});
	}
	
}
