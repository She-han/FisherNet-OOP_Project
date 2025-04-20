/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.dao;
import com.mysql.jdbc.ResultSet;
import fishernet.model.SensorData;
import fishernet.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ASUS
 */
public class SensorDAO {
    public static void saveSensorData(SensorData data) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO sensor_data (boat_id, sensor_type, value) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, data.getBoatId());
            ps.setString(2, data.getSensorType());
            ps.setString(3, data.getValue());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("❌ Sensor data insert failed: " + e.getMessage());
        }
    }
    
    public static List<SensorData> getLatestSensorData(int boatId) {
    List<SensorData> list = new ArrayList<>();
    try (Connection conn = DBConnection.getConnection()) {
        String sql = """
            SELECT sensor_type, value
            FROM sensor_data
            WHERE boat_id = ?
            ORDER BY timestamp DESC
            LIMIT 10
        """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, boatId);
        ResultSet rs = (ResultSet) ps.executeQuery();

        while (rs.next()) {
            list.add(new SensorData(boatId, rs.getString("sensor_type"), rs.getString("value")));
        }
    } catch (Exception e) {
        System.out.println("❌ Failed to load sensor data: " + e.getMessage());
    }
    return list;
}

}
