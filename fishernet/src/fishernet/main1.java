/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet;

import com.mysql.jdbc.Connection;
import fishernet.utils.DBConnection;

/**
 *
 * @author ASUS
 */
public class main1 {
    public static void main(String[] args) {
        // added to just check the database connection
        try {
            java.sql.Connection conn = DBConnection.getConnection();
            System.out.println("✅ Connected successfully");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
