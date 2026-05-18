package service;

import dao.RentalDAO;
import dao.PaymentDAO;
import dao.VehicleDAO;
import model.Rental;
import model.Payment;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * RentalService - Business logic for booking, returning vehicles, and payments.
 * Fine rate: 1.5x daily rate per extra day.
 */
public class RentalService {

    private final RentalDAO  rentalDAO  = new RentalDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private static final double FINE_MULTIPLIER = 1.5;

    /**
     * Books a vehicle for a customer.
     * @return "SUCCESS" or error message
     */
    public String bookVehicle(Rental r, String paymentMethod) {
        if (r.getCustomerId() <= 0) return "Please select a customer.";
        if (r.getVehicleId()  <= 0) return "Please select a vehicle.";
        if (r.getStartDate()  == null || r.getEndDate() == null) return "Dates are required.";
        if (!r.getEndDate().after(r.getStartDate())) return "End date must be after start date.";

        // Calculate total
        long days = ChronoUnit.DAYS.between(
            r.getStartDate().toLocalDate(), r.getEndDate().toLocalDate());
        double ppd = vehicleDAO.getById(r.getVehicleId()).getPricePerDay();
        r.setTotalAmount(days * ppd);
        r.setFineAmount(0);
        r.setStatus("Active");

        int rentalId = rentalDAO.createRental(r);
        if (rentalId < 0) return "Booking failed. Please try again.";

        // Mark vehicle as Rented
        vehicleDAO.updateStatus(r.getVehicleId(), "Rented");

        // Record payment
        Payment p = new Payment();
        p.setRentalId(rentalId);
        p.setAmountPaid(r.getTotalAmount());
        p.setPaymentMethod(paymentMethod);
        paymentDAO.recordPayment(p);

        return "SUCCESS";
    }

    /**
     * Processes vehicle return.
     * Calculates late fine if returned after end_date.
     * @return fine amount (0 if on time)
     */
    public double returnVehicle(int rentalId, Date actualReturnDate) {
        Rental r = rentalDAO.getById(rentalId);
        if (r == null) return -1;

        double fine = 0;
        LocalDate planned = r.getEndDate().toLocalDate();
        LocalDate actual  = actualReturnDate.toLocalDate();

        if (actual.isAfter(planned)) {
            long extraDays = ChronoUnit.DAYS.between(planned, actual);
            double ppd = vehicleDAO.getById(r.getVehicleId()).getPricePerDay();
            fine = extraDays * ppd * FINE_MULTIPLIER;
        }

        rentalDAO.returnVehicle(rentalId, actualReturnDate, fine);
        vehicleDAO.updateStatus(r.getVehicleId(), "Available");

        if (fine > 0) {
            Payment p = new Payment();
            p.setRentalId(rentalId);
            p.setAmountPaid(fine);
            p.setPaymentMethod("Cash");
            paymentDAO.recordPayment(p);
        }
        return fine;
    }

    public List<Rental> getAllRentals()            { return rentalDAO.getAllRentals(); }
    public List<Rental> searchRentals(String kw)  { return rentalDAO.searchRentals(kw); }
    public List<Rental> getActiveRentals()         { return rentalDAO.getActiveRentals(); }
    public Rental       getById(int id)            { return rentalDAO.getById(id); }
    public int          countActive()              { return rentalDAO.countActive(); }

    public List<model.Payment> getAllPayments()              { return paymentDAO.getAllPayments(); }
    public List<model.Payment> searchPayments(String kw)    { return paymentDAO.searchPayments(kw); }
    public double              totalRevenue()               { return paymentDAO.totalRevenue(); }
}
