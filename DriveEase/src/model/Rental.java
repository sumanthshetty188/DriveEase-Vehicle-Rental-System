package model;

import java.sql.Date;

/**
 * Rental model — maps to the `rentals` table.
 */
public class Rental {
    private int    rentalId;
    private int    customerId;
    private int    vehicleId;
    private Date   startDate;
    private Date   endDate;
    private Date   actualReturnDate;
    private double totalAmount;
    private double fineAmount;
    private String status;
    private int    createdBy;

    // Joined display fields (not columns)
    private String customerName;
    private String vehicleName;
    private String vehicleRegNo;

    public Rental() {}

    // ── Getters & Setters ──────────────────────────────
    public int    getRentalId()                   { return rentalId; }
    public void   setRentalId(int id)             { this.rentalId = id; }

    public int    getCustomerId()                 { return customerId; }
    public void   setCustomerId(int id)           { this.customerId = id; }

    public int    getVehicleId()                  { return vehicleId; }
    public void   setVehicleId(int id)            { this.vehicleId = id; }

    public Date   getStartDate()                  { return startDate; }
    public void   setStartDate(Date d)            { this.startDate = d; }

    public Date   getEndDate()                    { return endDate; }
    public void   setEndDate(Date d)              { this.endDate = d; }

    public Date   getActualReturnDate()           { return actualReturnDate; }
    public void   setActualReturnDate(Date d)     { this.actualReturnDate = d; }

    public double getTotalAmount()                { return totalAmount; }
    public void   setTotalAmount(double a)        { this.totalAmount = a; }

    public double getFineAmount()                 { return fineAmount; }
    public void   setFineAmount(double f)         { this.fineAmount = f; }

    public String getStatus()                     { return status; }
    public void   setStatus(String s)             { this.status = s; }

    public int    getCreatedBy()                  { return createdBy; }
    public void   setCreatedBy(int u)             { this.createdBy = u; }

    public String getCustomerName()               { return customerName; }
    public void   setCustomerName(String n)       { this.customerName = n; }

    public String getVehicleName()                { return vehicleName; }
    public void   setVehicleName(String n)        { this.vehicleName = n; }

    public String getVehicleRegNo()               { return vehicleRegNo; }
    public void   setVehicleRegNo(String r)       { this.vehicleRegNo = r; }
}
