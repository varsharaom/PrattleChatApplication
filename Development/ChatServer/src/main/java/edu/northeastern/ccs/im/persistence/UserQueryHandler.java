package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.serverim.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class UserQueryHandler extends QueryHandler {
	
	public UserQueryHandler() {
		super();
	}
	
    public User createUser(String userName, String pass, String nickName) {
    		Date date = new Date(System.currentTimeMillis());
    		String query = String.format("INSERT into %s (%s,%s,%s,%s) VALUES('%s','%s','%s',%tc);", DBConstants.USER_TABLE,
    					DBConstants.USER_USERNAME, DBConstants.USER_PASS, DBConstants.USER_NICKNAME, DBConstants.USER_LAST_SEEN,
    					userName, pass, nickName, date);
        long id = doInsertQuery(query);
        if (id != -1) {
        		return new User(id, userName, nickName, date.getTime());
        }
        return null;
    }

    public void updateUserLastLogin(User user) {
    		String query = String.format("UPDATE %s set %s = %d WHERE %s = %d;", 
    				DBConstants.USER_TABLE,  DBConstants.USER_LAST_SEEN, System.currentTimeMillis(),
    				DBConstants.USER_ID, user.getUserID());
        doUpdateQuery(query);
    }

    public List<Long> getCircles(User user) {
    		long userId = user.getUserID();
    		String query = String.format("SELECT * from %s WHERE %s = %d OR %s = %d;",
    				DBConstants.CIRCLES_TABLE, DBConstants.CIRCLE_USER_1_ID, user.getUserID(),
				DBConstants.CIRCLE_USER_2_ID, user.getUserID());
    		List<Long> circle = new ArrayList<>();
    		ResultSet rs = doSelectQuery(query);
    		if (rs != null) {
	        try {
				while(rs.next()) {
						if(rs.getLong(2) != userId) {
							circle.add(rs.getLong(2));
						}
						if(rs.getLong(3) != userId) {
							circle.add(rs.getLong(3));
						}
				}
			} catch (SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG);
			}
    		}
        return circle;
    }
    
    public String getPassword(String name) {
    		String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
    				DBConstants.USER_PASS, DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
    		ResultSet rs = doSelectQuery(query);
    		if (rs != null) {
    			try {
    				rs.next();
				return rs.getString(1);
    			} catch(SQLException e) {
    				logger.log(Level.INFO, SQL_EXCEPTION_MSG);
    			}
    		}
    		return "";
    }
    
    public boolean checkUserNameExists(String name) {
    		String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
				DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
		ResultSet rs = doSelectQuery(query);
		if (rs != null) {
			try {
				return rs.next();
			} catch(SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG);
			}
		}
		return false;
    }

}