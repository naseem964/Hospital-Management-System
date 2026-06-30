# 🏥 Relational Hospital Management System

**Author:** Naseem Jabir
**Course:** CISC 4900 - Senior Capstone  
**Term:** Summer 2026  

##  Project Abstract
The **Relational Hospital Management System** is a secure, 3-tier desktop application engineered to replace error-prone text-based medical records. Built with JavaFX and a MySQL Central Dogma database, this application prioritizes **Defensive UI/UX Design** and **Data Integrity** to protect highly vulnerable patient and personnel data.

##  Core Features
* **Full CRUD Operations:** Seamlessly Create, Read, Update, and Delete subject (patient) and personnel (doctor) records.
* **Relational Integrity:** Enforces strict Foreign Key constraints (`doctor_id`) to prevent "ghost assignments" (assigning patients to non-existent doctors).
* **Defensive UX (State-Blocking):** Implements modal `Alert` dialogs to pause application execution and verify authorization before any catastrophic `DELETE` commands are sent to the database.
* **In-Memory Search Filtering:** Utilizes Java `FilteredList` to instantly sort and query patient records in application RAM, drastically reducing expensive server queries.
* **DAO Bridging Pattern:** Separates the visual JavaFX environment from backend logic using Data Access Objects (`PatientDAO`, `DoctorDAO`) for clean, modular code.
* **SQL Injection Protection:** Secures all database mutations using parameterized `PreparedStatement` interfaces.

## Technology Stack
* **Language:** Java 17
* **Presentation Layer:** JavaFX
* **Database:** MySQL
* **Local Server Environment:** XAMPP (Apache/MySQL)
* **API Bridge:** JDBC (Java Database Connectivity)
* **IDE:** IntelliJ IDEA

---

## Installation & Setup Guide

To run this application locally, you must recreate the database environment and configure the JavaFX execution path.

### 1. Database Configuration
1. Install and launch the **XAMPP Control Panel**.
2. Start the **Apache** and **MySQL** modules.
3. Open `phpMyAdmin` (usually at `http://localhost/phpmyadmin`).
4. Create a new database named `hospital_db`.
5. Run the following SQL commands to build the relational schema:
   ```sql
   CREATE TABLE doctors (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       specialty VARCHAR(100) NOT NULL
   );

   CREATE TABLE patients (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       age INT NOT NULL,
       ailment VARCHAR(100) NOT NULL,
       doctor_id INT,
       CONSTRAINT fk_assigned_personnel FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL
   );
