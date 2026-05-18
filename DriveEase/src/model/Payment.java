package model;

import java.sql.Timestamp;

/**
 * Payment model — maps to the `payments` table.
 */
public class Payment {
    private int       paymentId;
    private int       rentalId;
    private double    amountPaid;
    private Timestamp paymentDate;
    private String    paymentMethod;

    // Joined display fields
    private String customerName;
    private String vehicleName;

    public Payment() {}

    // ── Getters & Setters ──────────────────────────────
    public int       getPaymentId()               { return paymentId; }
    public void      setPaymentId(int id)         { this.paymentId = id; }

    public int       getRentalId()                { return rentalId; }
    public void      setRentalId(int id)          { this.rentalId = id; }

    public double    getAmountPaid()              { return amountPaid; }
    public void      setAmountPaid(double a)      { this.amountPaid = a; }

    public Timestamp getPaymentDate()             { return paymentDate; }
    public void      setPaymentDate(Timestamp t)  { this.paymentDate = t; }

    public String    getPaymentMethod()           { return paymentMethod; }
    public void      setPaymentMethod(String m)   { this.paymentMethod = m; }

    public String    getCustomerName()            { return customerName; }
    public void      setCustomerName(String n)    { this.customerName = n; }

    public String    getVehicleName()             { return vehicleName; }
    public void      setVehicleName(String n)     { this.vehicleName = n; }
}
