Authentication Service
The Authentication Service is responsible for generating and managing information about transactions. Each transaction includes the following details:

ID (integer)
Timestamp
Type (string)
Actor (string)
Transaction Data (key-value map of strings)
Overview
This service receives transaction data via HTTP requests, stores them in a MySQL database, and provides CRUD (Create, Read, Update, Delete) and search operations via HTTP interface.

Features
Create Transaction: Add a new transaction with the specified details.
Update Transaction: Modify an existing transaction's information.
Delete Transaction: Remove a transaction from the database.
Search Transactions: Search for transactions based on specific criteria.
Technologies Used
Java Spring Boot
MySQL Database
HTTP API
Setup
Clone the repository to your local machine.
Configure your MySQL database connection in application.properties.
Build and run the Spring application.
