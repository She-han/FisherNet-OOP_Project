/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.dao;

import fishernet.utils.DBConnection;
import java.sql.*;
 public class UserDAO {
    public static boolean login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // login success if user exists
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }
 }
