package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.im.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class UserQueryHandler extends QueryHandler {
	
    public User createUser(String userName, String nickName) {
    		long time = System.currentTimeMillis();
        String query = "Insert into " + QueryConstants.USER_TABLE
                + " values(" + userName + "," + nickName
                + "," + time + ")";
        long id = doInsertQuery(query);
        if (id != -1) {
        		return new User(id, userName, nickName, time);
        }
        return null;
    }

    public void updateUserLastLogin(User user) {
        String query = "Update " + QueryConstants.USER_TABLE + " set "
            + QueryConstants.USER_LAST_LOGIN + " = " + System.currentTimeMillis()
            + " where  " + QueryConstants.USER_ID + " = " + user.getUserID();
        doUpdateQuery(query);
    }

    public List<Long> getCircles(User user) {
    		long userId = user.getUserID();
    		String query = "Select * from " + QueryConstants.CIRCLES_TABLE + " where "
				+ QueryConstants.CIRCLE_USER_1_ID + " = " + user.getUserID() + " OR "
				+ QueryConstants.CIRCLE_USER_2_ID + " = " + user.getUserID() + ";";
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

}
