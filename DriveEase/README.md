# DriveEase – Vehicle Rental System
### Java Swing + JDBC + MySQL Desktop Application

---

## Prerequisites
| Tool | Version |
|------|---------|
| JDK  | 17+     |
| MySQL| 8.0+    |
| Eclipse IDE | 2022+ |
| MySQL Connector/J | 8.x |

---

## Step-by-Step Setup

### 1. Database Setup
1. Open MySQL Workbench or any MySQL client.
2. Run the `driveease.sql` file:
3. Verify the `driveease_db` database and all tables are created.

### 2. Configure DB Connection
Open `src/db/DBConnection.java` and update:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/driveease_db";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password";
src/
├── db/       → Database connection (DBConnection.java)
├── model/    → POJO data classes
├── dao/      → Direct SQL queries (PreparedStatements)
├── service/  → Business logic + validation
├── ui/       → Java Swing frames/panels
└── util/     → Password hashing, validation, theme, PDF

---

## Architecture Diagram

```mermaid
flowchart TB
    subgraph UI["UI Layer (Swing)"]
        L[LoginFrame]
        D[DashboardFrame]
        V[VehiclePanel]
        C[CustomerPanel]
        R[RentalPanel]
        T[TransactionPanel]
        RP[ReportPanel]
        FP[ForgotPasswordDialog]
    end

    subgraph SVC["Service Layer"]
        AS[AuthService]
        VS[VehicleService]
        CS[CustomerService]
        RS[RentalService]
    end

    subgraph DAO["DAO Layer"]
        UD[UserDAO]
        VD[VehicleDAO]
        CD[CustomerDAO]
        RD[RentalDAO]
        PD[PaymentDAO]
    end

    subgraph UTIL["Util"]
        PW[PasswordUtil\nSHA-256]
        VAL[ValidationUtil]
        THEME[UITheme]
        PDF[PDFGenerator]
    end

    DB[(MySQL\ndriveease_db)]
    CONN[DBConnection\nSingleton]

    L --> AS
    D --> VS & CS & RS
    V --> VS
    C --> CS
    R --> RS & VS & CS
    T --> RS
    RP --> RS & PDF
    FP --> AS

    AS --> UD
    VS --> VD
    CS --> CD
    RS --> RD & PD

    UD & VD & CD & RD & PD --> CONN --> DB
