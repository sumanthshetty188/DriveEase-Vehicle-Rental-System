package model;

/**
 * Vehicle model — maps to the `vehicles` table.
 */
public class Vehicle {
    private int    vehicleId;
    private String vehicleName;
    private String brand;
    private String vehicleType;
    private String registrationNumber;
    private double pricePerDay;
    private String availabilityStatus;

    public Vehicle() {}

    public Vehicle(String vehicleName, String brand, String vehicleType,
                   String regNo, double pricePerDay, String status) {
        this.vehicleName          = vehicleName;
        this.brand                = brand;
        this.vehicleType          = vehicleType;
        this.registrationNumber   = regNo;
        this.pricePerDay          = pricePerDay;
        this.availabilityStatus   = status;
    }

    // ── Getters & Setters ──────────────────────────────
    public int    getVehicleId()                    { return vehicleId; }
    public void   setVehicleId(int id)              { this.vehicleId = id; }

    public String getVehicleName()                  { return vehicleName; }
    public void   setVehicleName(String n)          { this.vehicleName = n; }

    public String getBrand()                        { return brand; }
    public void   setBrand(String b)                { this.brand = b; }

    public String getVehicleType()                  { return vehicleType; }
    public void   setVehicleType(String t)          { this.vehicleType = t; }

    public String getRegistrationNumber()           { return registrationNumber; }
    public void   setRegistrationNumber(String r)   { this.registrationNumber = r; }

    public double getPricePerDay()                  { return pricePerDay; }
    public void   setPricePerDay(double p)          { this.pricePerDay = p; }

    public String getAvailabilityStatus()           { return availabilityStatus; }
    public void   setAvailabilityStatus(String s)   { this.availabilityStatus = s; }

    @Override
    public String toString() {
        return vehicleId + " – " + vehicleName + " (" + registrationNumber + ")";
    }
}
