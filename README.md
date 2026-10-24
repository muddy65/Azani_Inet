# Azani ISP Management System

[![Download for Windows](https://img.shields.io/badge/Download-.EXE_for_Windows-blue?style=for-the-badge&logo=windows)](https://github.com/muddy65/Azani_Inet/releases/download/v1.0/Azani.Billing-1.0.exe)

## Overview
The Azani ISP Management System is a full-stack desktop application developed using **Java Swing** and **MySQL**. It is designed to automate the onboarding, billing, and infrastructural management of learning institutions (Primary, Junior, Senior Schools, and Colleges) seeking internet connectivity.

This project was built to satisfy the strict business logic and automated reporting requirements of the **SCO200 - Object Oriented Programming II** examination. 

*Note: This application is now packaged as a standalone Windows executable. It includes an embedded Java runtime and connects automatically to a cloud-hosted Aiven MySQL database.*

## Download & Installation
You do not need to install Java or configure a database to run this application.

1. **[Click here to download Azani_Billing.exe](https://github.com/muddy65/Azani_Inet/releases/download/v1.0/Azani.Billing-1.0.exe)**
2. Once downloaded, double-click the `.exe` file to run the installer.
3. Follow the on-screen prompts. The application will be installed on your computer and a shortcut will be added to your Start Menu.
4. Launch "Azani Billing" to begin using the system.

## Features & Business Logic
The system strictly enforces the following business rules:
-   **Automated Onboarding:** Calculates mandatory Registration Fees (KSh 8,500), Installation Fees (KSh 10,000), and tiered LAN/Hardware pricing (Table 2 compliance).
-   **Dynamic Billing Manager:** Securely calculates standard monthly bandwidth costs (Table 1 compliance) and instantly applies a 10% discount for bandwidth upgrades.
-   **Automated Penalty Engine:** Automatically scans the database and shifts active clients to `OVERDUE` if they fail to pay by the end of the current month, appending a 15% fine.
-   **Automated Disconnection:** Flags `OVERDUE` clients as `DISCONNECTED` if their balance and fines are not cleared by the 10th day of the subsequent month.
-   **Reconnection Logic:** Mandates a KSh 1,000 reconnection fee alongside the base bill and fines before a disconnected client can be restored to `ACTIVE` status.
-   **Advanced Aggregated Reporting:** Generates live SQL-driven reports for Defaulters, Disconnected clients, Infrastructure Spend, Category Revenue, and Contact Directories.

## User Guide
-   **Dashboard:** View live aggregate statistics and a table of all currently active institutions.
-   **Registration:** Onboard new clients. Toggle the infrastructure readiness check to dynamically calculate hardware costs vs. installation fees. Click "Calculate Bill" to preview the initial transaction.
-   **Payments & Billing:** 
    *   Use the smart-search field to find a client. The system will auto-calculate their exact amount due (Base + Fines + Reconnection). 
    *   Use the Upgrade Calculator on the right to apply discounted bandwidth tiers.
-   **Reports:** Navigate between memory-safe generated reports. Select a Disconnected client and use the "Permanently Delete" button to wipe their records from the database entirely.

## System Architecture (For Developers)
-   **UI Layer:** Java Swing (Modern, flat-design UI with CardLayouts for seamless navigation).
-   **Service Layer (`BillingManager.java`):** A centralized, static class that handles all financial mathematics to prevent UI-level data manipulation.
-   **Data Access Object (DAO) Layer:** 
    *   `InstitutionDAO.java`: Handles registration, validation, automated status changes, and complex `GROUP BY` report generation.
    *   `PaymentDAO.java`: Handles transactional logic, fine clearing, and upgrade execution.
-   **Database:** Relational MySQL Database hosted on Aiven Cloud.