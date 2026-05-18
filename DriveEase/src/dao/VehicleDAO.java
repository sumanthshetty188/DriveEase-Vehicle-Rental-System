package dao;

import db.DBConnection;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * VehicleDAO - CRUD operations for the vehicles table.
 */
public class VehicleDAO {

    public boolean addVehicle(Vehicle v) {
        String sql = "INSERT INTO vehicles (vehicle_name, brand, vehicle_type, " +
                     "registration_number, price_per_day, availability_status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getVehicleName());
            ps.setString(2, v.getBrand());
            ps.setString(3, v.getVehicleType());
            ps.setString(4, v.getRegistrationNumber());
            ps.setDouble(5, v.getPricePerDay());
            ps.setString(6, v.getAvailabilityStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] addVehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVehicle(Vehicle v) {
        String sql = "UPDATE vehicles SET vehicle_name=?, brand=?, vehicle_type=?, " +
                     "registration_number=?, price_per_day=?, availability_status=? " +
                     "WHERE vehicle_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getVehicleName());
            ps.setString(2, v.getBrand());
            ps.setString(3, v.getVehicleType());
            ps.setString(4, v.getRegistrationNumber());
            ps.setDouble(5, v.getPricePerDay());
            ps.setString(6, v.getAvailabilityStatus());
            ps.setInt(7, v.getVehicleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] updateVehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] deleteVehicle: " + e.getMessage());
            return false;
        }
    }

    public List<Vehicle> getAllVehicles() {
        return searchVehicles("");
    }

    public List<Vehicle> searchVehicles(String keyword) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE vehicle_name LIKE ? " +
                     "OR brand LIKE ? OR registration_number LIKE ? OR vehicle_type LIKE ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k);
            ps.setString(3, k); ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] searchVehicles: " + e.getMessage());
        }
        return list;
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE availability_status = 'Available'";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] getAvailable: " + e.getMessage());
        }
        return list;
    }

    public Vehicle getById(int id) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] getById: " + e.getMessage());
        }
        return null;
    }

    /** Dashboard stat: total vehicle count. */
    public int countTotal() {
        return countByStatus(null);
    }

    /** Dashboard stat: count by status. */
    public int countByStatus(String status) {
        String sql = status == null
            ? "SELECT COUNT(*) FROM vehicles"
            : "SELECT COUNT(*) FROM vehicles WHERE availability_status = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            if (status != null) ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] count: " + e.getMessage());
        }
        return 0;
    }

    public boolean updateStatus(int vehicleId, String status) {
        String sql = "UPDATE vehicles SET availability_status=? WHERE vehicle_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] updateStatus: " + e.getMessage());
            return false;
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setVehicleName(rs.getString("vehicle_name"));
        v.setBrand(rs.getString("brand"));
        v.setVehicleType(rs.getString("vehicle_type"));
        v.setRegistrationNumber(rs.getString("registration_number"));
        v.setPricePerDay(rs.getDouble("price_per_day"));
        v.setAvailabilityStatus(rs.getString("availability_status"));
        return v;
    }
}
