package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {
    private static final Logger logger = Logger.getLogger(UserDao.class.getName());


    public User getDetails(String username){
        User user = new User();

        try{

            ResultSet rs;
            try (Connection conn = H2DatabaseConnection.getConnectionToDatabase();
                 PreparedStatement pstmt = conn.prepareStatement("select * from user where username=?")) {
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();

                while(rs.next()){
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setName(rs.getString("name"));
                    user.setGender(rs.getString("gender"));
                    user.setAge(rs.getInt("age"));
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "could not execute query", ex);
        }
        return user;
    }

}
