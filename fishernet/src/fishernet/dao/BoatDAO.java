package fishernet.dao;

import fishernet.model.Boat;
import fishernet.utils.DBConnection;
import java.sql.*;

public class BoatDAO {

    public static void saveBoat(Boat boat) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO boats (name, registration_no, qr_code_path, sensors) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, boat.getName());
            ps.setString(2, boat.getRegistrationNo());
            ps.setString(3, boat.getQrCodePath());
            ps.setString(4, boat.getSensors());
            ps.executeUpdate();

            System.out.println("✅ Boat saved to database.");
        } catch (SQLException e) {
            System.out.println("❌ Error saving boat: " + e.getMessage());
        }
    }

    public static Boat findByRegNo(String regNo) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM boats WHERE registration_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, regNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Boat boat = new Boat(
                    rs.getString("name"),
                    regNo,
                    rs.getString("qr_code_path"),
                    rs.getString("sensors")
                );
                boat.setId(rs.getInt("id"));
                return boat;
            }
        } catch (SQLException e) {
            System.out.println("❌ Boat lookup failed: " + e.getMessage());
        }
        return null;
    }
}
