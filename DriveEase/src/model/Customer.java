package model;

/**
 * Customer model — maps to the `customers` table.
 */
public class Customer {
    private int    customerId;
    private String fullName;
    private String phone;
    private String email;
    private String drivingLicense;
    private String address;

    public Customer() {}

    public Customer(String fullName, String phone, String email,
                    String drivingLicense, String address) {
        this.fullName       = fullName;
        this.phone          = phone;
        this.email          = email;
        this.drivingLicense = drivingLicense;
        this.address        = address;
    }

    // ── Getters & Setters ──────────────────────────────
    public int    getCustomerId()                 { return customerId; }
    public void   setCustomerId(int id)           { this.customerId = id; }

    public String getFullName()                   { return fullName; }
    public void   setFullName(String n)           { this.fullName = n; }

    public String getPhone()                      { return phone; }
    public void   setPhone(String p)              { this.phone = p; }

    public String getEmail()                      { return email; }
    public void   setEmail(String e)              { this.email = e; }

    public String getDrivingLicense()             { return drivingLicense; }
    public void   setDrivingLicense(String dl)    { this.drivingLicense = dl; }

    public String getAddress()                    { return address; }
    public void   setAddress(String a)            { this.address = a; }

    @Override
    public String toString() {
        return customerId + " – " + fullName + " (" + phone + ")";
    }
}
