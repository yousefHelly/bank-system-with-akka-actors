# Bank System with Akka Actors

This Scala project showcases a straightforward implementation of a bank system using Akka Actors. The system includes essential banking operations such as deposit, withdraw, transfer, and transaction management. What sets this project apart is its seamless integration with a MySQL database, ensuring persistent storage of transactional data.

## Features

### Deposit
Add funds to an account securely. Provide a convenient and reliable way for users to increase their account balance.

### Withdraw
Safely deduct funds from an account. Ensure the system handles withdrawals with precision and security.

### Transfer
Transfer money between accounts with transactional integrity. Guarantee that funds move seamlessly between accounts while maintaining data consistency.

### Transaction Management
Maintain a comprehensive log of transactions in a MySQL database. Track all financial activities, providing an audit trail for transparency and accountability.


## Prerequisites

Before running the project, make sure you have the following installed:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Scala Build Tool (SBT)](https://www.scala-sbt.org/)
- [MySQL](https://www.mysql.com/) database server
- [XAMPP](https://www.apachefriends.org/index.html) (or any other MySQL server setup)
- [Bank DB creation File](https://drive.google.com/file/d/1HL4l08u78Nid3YPMFDzVR1Cr2CMvUM0-)

## Installation

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/yousefHelly/bank-system-with-akka-actors.git

2. **Navigate to the project directory:**

   ```bash
   cd bank-system-with-akka-actors

3. **Configure MySQL Database Connection**:    

   - Open the `application.conf` file in your project directory.
     
   - Update the configuration with your MySQL database connection details:


2. Setting Up MySQL Database with XAMPP:
  - Start XAMPP and ensure that Apache and MySQL server are running.
  
  - Open the phpMyAdmin interface by visiting http://localhost/phpmyadmin/ in your web browser.
  
  - Create a new database for the project with the name ``bank`` and Import the database file that contains all the necessary tables for the application to run.

## Run the Application

   - In the project directory, execute the following command to run the application:

     ```bash
     sbt run
     ```

