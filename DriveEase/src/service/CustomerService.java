package service;

import dao.CustomerDAO;
import model.Customer;
import util.ValidationUtil;
import java.util.List;

/**
 * CustomerService - Business logic for customer management.
 */
public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public String addCustomer(Customer c) {
        if (ValidationUtil.isNullOrEmpty(c.getFullName()))    return "Full name is required.";
        if (!ValidationUtil.isValidPhone(c.getPhone()))       return "Invalid phone number (10 digits required).";
        if (!ValidationUtil.isNullOrEmpty(c.getEmail()) &&
            !ValidationUtil.isValidEmail(c.getEmail()))       return "Invalid email address.";
        if (ValidationUtil.isNullOrEmpty(c.getDrivingLicense())) return "Driving license is required.";

        boolean ok = customerDAO.addCustomer(c);
        return ok ? "SUCCESS" : "Failed. License number may already exist.";
    }

    public String updateCustomer(Customer c) {
        if (c.getCustomerId() <= 0) return "Invalid customer ID.";
        if (!ValidationUtil.isValidPhone(c.getPhone())) return "Invalid phone number.";
        boolean ok = customerDAO.updateCustomer(c);
        return ok ? "SUCCESS" : "Update failed.";
    }

    public String deleteCustomer(int id) {
        boolean ok = customerDAO.deleteCustomer(id);
        return ok ? "SUCCESS" : "Cannot delete – customer may have active rentals.";
    }

    public List<Customer> getAllCustomers()             { return customerDAO.getAllCustomers(); }
    public List<Customer> searchCustomers(String kw)   { return customerDAO.searchCustomers(kw); }
    public Customer       getById(int id)              { return customerDAO.getById(id); }
    public int            countTotal()                 { return customerDAO.countTotal(); }
}
