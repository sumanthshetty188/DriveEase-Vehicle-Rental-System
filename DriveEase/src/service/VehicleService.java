package service;

import dao.VehicleDAO;
import model.Vehicle;
import java.util.List;

/**
 * VehicleService - Business logic layer for vehicle operations.
 */
public class VehicleService {

    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public String addVehicle(Vehicle v) {
        if (v.getVehicleName().trim().isEmpty()) return "Vehicle name is required.";
        if (v.getBrand().trim().isEmpty())       return "Brand is required.";
        if (v.getRegistrationNumber().trim().isEmpty()) return "Registration number is required.";
        if (v.getPricePerDay() <= 0)             return "Price per day must be greater than 0.";

        boolean ok = vehicleDAO.addVehicle(v);
        return ok ? "SUCCESS" : "Failed to add vehicle. Registration number may already exist.";
    }

    public String updateVehicle(Vehicle v) {
        if (v.getVehicleId() <= 0)               return "Invalid vehicle ID.";
        if (v.getPricePerDay() <= 0)             return "Price per day must be greater than 0.";
        boolean ok = vehicleDAO.updateVehicle(v);
        return ok ? "SUCCESS" : "Update failed.";
    }

    public String deleteVehicle(int vehicleId) {
        boolean ok = vehicleDAO.deleteVehicle(vehicleId);
        return ok ? "SUCCESS" : "Cannot delete – vehicle may have active rentals.";
    }

    public List<Vehicle> getAllVehicles()             { return vehicleDAO.getAllVehicles(); }
    public List<Vehicle> searchVehicles(String kw)   { return vehicleDAO.searchVehicles(kw); }
    public List<Vehicle> getAvailableVehicles()      { return vehicleDAO.getAvailableVehicles(); }
    public Vehicle       getById(int id)             { return vehicleDAO.getById(id); }
    public int           countTotal()                { return vehicleDAO.countTotal(); }
    public int           countByStatus(String s)     { return vehicleDAO.countByStatus(s); }
}
