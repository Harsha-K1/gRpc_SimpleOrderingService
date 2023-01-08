package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDao {
    private static final Logger logger =Logger.getLogger(OrderDao.class.getName());

    public List<Order> getOrders(int userId){
        List<Order> orders = new ArrayList<Order>();
        ResultSet rs;
        try (Connection conn = H2DatabaseConnection.getConnectionToDatabase();
             PreparedStatement pstmt = conn.prepareStatement("select * from orders where user_id = ?")) {
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while(rs.next()){
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setNumOfItems(rs.getInt("no_of_items"));
                order.setTotalAmt(rs.getDouble("total_amount"));
                order.setOrderDate(rs.getDate("order_date"));
                orders.add(order);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Exception occurrec while fetching orders!", ex);
        }
        return orders;
    }

}
