package dao;

import db.DBConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO - CRUD operations for the customers table.
 */
public class CustomerDAO {

    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO customers (full_name, phone, email, driving_license, address) " +
                     "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDrivingLicense());
            ps.setString(5, c.getAddress());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] addCustomer: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE customers SET full_name=?, phone=?, email=?, " +
                     "driving_license=?, address=? WHERE customer_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDrivingLicense());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateCustomer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] deleteCustomer: " + e.getMessage());
            return false;
        }
    }

    public List<Customer> getAllCustomers() {
        return searchCustomers("");
    }

    public List<Customer> searchCustomers(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE full_name LIKE ? OR phone LIKE ? " +
                     "OR email LIKE ? OR driving_license LIKE ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k);
            ps.setString(3, k); ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] search: " + e.getMessage());
        }
        return list;
    }

    public Customer getById(int id) {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getById: " + e.getMessage());
        }
        return null;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] count: " + e.getMessage());
        }
        return 0;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setFullName(rs.getString("full_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setDrivingLicense(rs.getString("driving_license"));
        c.setAddress(rs.getString("address"));
        return c;
    }
}
