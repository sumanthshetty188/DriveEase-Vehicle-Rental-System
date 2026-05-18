package dao;

import db.DBConnection;
import model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentDAO - Database operations for the payments table.
 */
public class PaymentDAO {

    public boolean recordPayment(Payment p) {
        String sql = "INSERT INTO payments (rental_id, amount_paid, payment_method) VALUES (?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, p.getRentalId());
            ps.setDouble(2, p.getAmountPaid());
            ps.setString(3, p.getPaymentMethod());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] recordPayment: " + e.getMessage());
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        return searchPayments("");
    }

    public List<Payment> searchPayments(String keyword) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, c.full_name AS customer_name, v.vehicle_name " +
                     "FROM payments p " +
                     "JOIN rentals r ON p.rental_id = r.rental_id " +
                     "JOIN customers c ON r.customer_id = c.customer_id " +
                     "JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                     "WHERE c.full_name LIKE ? OR v.vehicle_name LIKE ? " +
                     "OR p.payment_method LIKE ? " +
                     "ORDER BY p.payment_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("payment_id"));
                p.setRentalId(rs.getInt("rental_id"));
                p.setAmountPaid(rs.getDouble("amount_paid"));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                p.setPaymentMethod(rs.getString("payment_method"));
                p.setCustomerName(rs.getString("customer_name"));
                p.setVehicleName(rs.getString("vehicle_name"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] search: " + e.getMessage());
        }
        return list;
    }

    public double totalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount_paid),0) FROM payments";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[PaymentDAO] totalRevenue: " + e.getMessage());
        }
        return 0;
    }
}
