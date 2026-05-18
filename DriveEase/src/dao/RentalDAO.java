package dao;

import db.DBConnection;
import model.Rental;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RentalDAO - Database operations for rentals table.
 */
public class RentalDAO {

    /** Creates a new rental record and returns generated rental_id. */
    public int createRental(Rental r) {
        String sql = "INSERT INTO rentals (customer_id, vehicle_id, start_date, end_date, " +
                     "total_amount, fine_amount, status, created_by) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection()
                 .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getCustomerId());
            ps.setInt(2, r.getVehicleId());
            ps.setDate(3, r.getStartDate());
            ps.setDate(4, r.getEndDate());
            ps.setDouble(5, r.getTotalAmount());
            ps.setDouble(6, r.getFineAmount());
            ps.setString(7, r.getStatus());
            ps.setInt(8, r.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RentalDAO] createRental: " + e.getMessage());
        }
        return -1;
    }

    /** Marks a rental as Returned, sets actual return date and fine. */
    public boolean returnVehicle(int rentalId, Date returnDate, double fine) {
        String sql = "UPDATE rentals SET status='Returned', actual_return_date=?, " +
                     "fine_amount=? WHERE rental_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, returnDate);
            ps.setDouble(2, fine);
            ps.setInt(3, rentalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RentalDAO] returnVehicle: " + e.getMessage());
            return false;
        }
    }

    public List<Rental> getAllRentals() {
        return searchRentals("");
    }

    public List<Rental> searchRentals(String keyword) {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name AS customer_name, " +
                     "v.vehicle_name, v.registration_number " +
                     "FROM rentals r " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                     "WHERE c.full_name LIKE ? OR v.vehicle_name LIKE ? " +
                     "OR r.status LIKE ? OR v.registration_number LIKE ? " +
                     "ORDER BY r.created_at DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k);
            ps.setString(3, k); ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RentalDAO] searchRentals: " + e.getMessage());
        }
        return list;
    }

    public Rental getById(int rentalId) {
        String sql = "SELECT r.*, c.full_name AS customer_name, " +
                     "v.vehicle_name, v.registration_number " +
                     "FROM rentals r " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                     "WHERE r.rental_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RentalDAO] getById: " + e.getMessage());
        }
        return null;
    }

    public List<Rental> getActiveRentals() {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name AS customer_name, " +
                     "v.vehicle_name, v.registration_number " +
                     "FROM rentals r " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                     "WHERE r.status = 'Active' ORDER BY r.end_date ASC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RentalDAO] getActive: " + e.getMessage());
        }
        return list;
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM rentals WHERE status = 'Active'";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RentalDAO] countActive: " + e.getMessage());
        }
        return 0;
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        Rental r = new Rental();
        r.setRentalId(rs.getInt("rental_id"));
        r.setCustomerId(rs.getInt("customer_id"));
        r.setVehicleId(rs.getInt("vehicle_id"));
        r.setStartDate(rs.getDate("start_date"));
        r.setEndDate(rs.getDate("end_date"));
        r.setActualReturnDate(rs.getDate("actual_return_date"));
        r.setTotalAmount(rs.getDouble("total_amount"));
        r.setFineAmount(rs.getDouble("fine_amount"));
        r.setStatus(rs.getString("status"));
        r.setCustomerName(rs.getString("customer_name"));
        r.setVehicleName(rs.getString("vehicle_name"));
        r.setVehicleRegNo(rs.getString("registration_number"));
        return r;
    }
}
