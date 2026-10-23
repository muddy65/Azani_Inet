# Azani ISP Management System

## Overview
The Azani ISP Management System is a full-stack desktop application developed using **Java Swing** and **MySQL**. It is designed to automate the onboarding, billing, and infrastructural management of learning institutions (Primary, Junior, Senior Schools, and Colleges) seeking internet connectivity.

This project was built to satisfy the strict business logic and automated reporting requirements of the **SCO200 - Object Oriented Programming II** examination.

## Features & Business Logic
The system strictly enforces the following business rules:
*   **Automated Onboarding:** Calculates mandatory Registration Fees (KSh 8,500), Installation Fees (KSh 10,000), and tiered LAN/Hardware pricing (Table 2 compliance).
*   **Dynamic Billing Manager:** Securely calculates standard monthly bandwidth costs (Table 1 compliance) and instantly applies a 10% discount for bandwidth upgrades.
*   **Automated Penalty Engine:** Automatically scans the database and shifts active clients to `OVERDUE` if they fail to pay by the end of the current month, appending a 15% fine.
*   **Automated Disconnection:** Flags `OVERDUE` clients as `DISCONNECTED` if their balance and fines are not cleared by the 10th day of the subsequent month.
*   **Reconnection Logic:** Mandates a KSh 1,000 reconnection fee alongside the base bill and fines before a disconnected client can be restored to `ACTIVE` status.
*   **Advanced Aggregated Reporting:** Generates live SQL-driven reports for Defaulters, Disconnected clients, Infrastructure Spend, Category Revenue, and Contact Directories.

## System Architecture
*   **UI Layer:** Java Swing (Modern, flat-design UI with CardLayouts for seamless navigation).
*   **Service Layer (`BillingManager.java`):** A centralized, static class that handles all financial mathematics to prevent UI-level data manipulation.
*   **Data Access Object (DAO) Layer:** 
    *   `InstitutionDAO.java`: Handles registration, validation, automated status changes, and complex `GROUP BY` report generation.
    *   `PaymentDAO.java`: Handles transactional logic, fine clearing, and upgrade execution.
*   **Database:** Relational MySQL Database (`azani_isp`) utilizing tables for `institutions`, `subscriptions`, and `payments`.

## Setup & Installation
1. **Database Configuration:**
   * Open MySQL Workbench or phpMyAdmin.
   * Create a database named `azani_isp`.
   * Ensure your `DatabaseHelper.java` file contains your correct MySQL root username and password.
2. **Compilation:**
   * Import the project into your IDE (VS Code, IntelliJ, Eclipse, or NetBeans).
   * Ensure the **MySQL JDBC Driver** (`mysql-connector-java.jar`) is added to your project's build path/referenced libraries.
3. **Execution:**
   * Run the `MainDashboard.java` file to launch the application.

## User Guide
*   **Dashboard:** View live aggregate statistics and a table of all currently active institutions.
*   **Registration:** Onboard new clients. Toggle the infrastructure readiness check to dynamically calculate hardware costs vs. installation fees. Click "Calculate Bill" to preview the initial transaction.
*   **Payments & Billing:** 
    *   Use the smart-search field to find a client. The system will auto-calculate their exact amount due (Base + Fines + Reconnection). 
    *   Use the Upgrade Calculator on the right to apply discounted bandwidth tiers.
*   **Reports:** Navigate between memory-safe generated reports. Select a Disconnected client and use the "Permanently Delete" button to wipe their records from the database entirely.