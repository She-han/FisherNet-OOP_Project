/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import java.sql.DriverManager;
//import com.mysql.jdbc.Connection;
 public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3308/fishernet_db";
    private static final String USER = "root"; // your username
    private static final String PASS = ""; // your password
    
    
    public static Connection getConnection() throws SQLException {
        return (Connection) DriverManager.getConnection(URL, USER, PASS);
    }
 }
