# Azani ISP Management Dashboard 🌐

A modern, desktop-based Java application designed to streamline the management of institutional internet subscriptions, billing, and network infrastructure setups for Azani ISP. Built as an academic project (SCO200), this system replaces manual record-keeping with a robust, database-driven dashboard.

## 🚀 Key Features

*   **Dashboard Overview:** Real-time KPI cards displaying total registered institutions, monthly recurring revenue, active defaulters, and pending disconnects. Includes a live feed of recent registrations.
*   **Client Onboarding (Registration):** A comprehensive intake form capturing contact details, requested bandwidth tiers, and hardware requirements (PCs and LAN nodes). Includes strict input validation.
*   **Billing & Payments:** Process monthly bills, installation fees, and hardware purchases. Features a dynamic "Upgrade Calculator" that automatically applies a 10% discount for bandwidth upgrades.
*   **Reporting & Analytics:** A modern tabbed interface to quickly view and manage institutions with overdue payments (Defaulters) or suspended services (Disconnected).

## 🛠️ Tech Stack

*   **Language:** Java (JDK 17+)
*   **GUI Framework:** Java Swing (Styled to mimic modern web interfaces)
*   **Database:** MySQL
*   **Architecture Pattern:** MVC (Model-View-Controller)

## 📂 Project Structure (MVC Architecture)

The application is cleanly separated into logical layers to ensure scalability and maintainability:

*   `com.azani.model` **(Models):** POJOs (`Institution`, `ServicePackage`, `PaymentRecord`) that mirror database tables.
*   `com.azani.dao` **(Data Access Objects):** Handles all MySQL database interactions (`InstitutionDAO`, `PaymentDAO`, `DatabaseHelper`).
*   `com.azani.service` **(Services):** Core business logic, including `BillingManager` for cost calculations and `ValidationService` for form sanitization.
*   `com.azani.ui` **(Views):** The graphical user interface, built with nested layouts and custom styling to provide a flat, web-like aesthetic.

## 🎨 UI/UX Design

The application breaks away from traditional "clunky" Java Swing designs by implementing:
*   A custom color palette (Teal `#20C997`, Navy Blue `#1A202C`, Web Gray `#F8FAFC`).
*   Flat, borderless `JTable` designs with custom cell spacing.
*   Interactive, tab-like buttons using `CardLayout`.
*   Extensive use of padding and `CompoundBorders` to create clean "White Card" data containers.

## ⚙️ Setup and Installation

### Prerequisites
1.  **Java Development Kit (JDK):** Ensure Java is installed on your system.
2.  **MySQL Server:** Running locally (default port `3306`).
3.  **MySQL JDBC Driver:** `mysql-connector-j.jar` is required to connect Java to MySQL.

### Database Configuration
1. Open your MySQL client (e.g., MySQL Workbench or terminal).
2. Create the database: `CREATE DATABASE azani_isp;`
3. Execute the provided `schema.sql` script to generate the required tables (`institutions`, `subscriptions`, `infrastructure`, `payments`).
4. Update `DatabaseHelper.java` with your specific MySQL username and password if they differ from the defaults.

### Running the Application

To compile and run the application from the terminal, ensure you include the MySQL connector in your classpath. 

**Compile:**
```bash
javac com/azani/ui/MainDashboard.java com/azani/ui/Panels/*.java
```

**Run (Linux/Mac):**
```bash
java -cp ".:../lib/mysql-connector-j.jar" com.azani.ui.MainDashboard
```
*(Note: Adjust the path to the `.jar` file based on where you stored it in your project folder).*

## 🔮 Future Enhancements
*   **Automated Status Updates:** A background service to automatically flag clients as 'OVERDUE' if payment is not recorded by the 5th of the month.
*   **Export Functionality:** Allow users to export the Defaulters and Disconnected reports to CSV or PDF formats.
*   **Authentication:** Add a login screen with role-based access control (Admin vs. Standard User).