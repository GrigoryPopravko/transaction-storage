## The Task

Authentication service generates information about transactions. Each transaction contains following information:

- ID (integer)

- Timestamp
- Type (string)
- Actor (string)
- Transaction data (key-value map of strings)



The transactions must be collected by a **new service**. The service should receive the data at HTTP interface, store them in SQL database and make them available via the HTTP interface.

Implement the service for CRUD (Create Update Delete) and search operations. Suggest and design what the search operation should look like to be usable.

Implement it as a Spring application using MySQL database.

## Overview

This service receives transaction data via HTTP requests, stores them in a MySQL database, and provides CRUD (Create, Read, Update, Delete) and search operations via HTTP interface.

## Performance and Optimization
Please note that the current implementation of the service does not include optimization for database performance. Depending on the expected load and usage patterns, you may need to consider database indexing, caching, and other optimizations to ensure efficient operation.

## Swagger
You can explore and interact with the service's API endpoints using the Swagger UI. Access the Swagger UI by navigating to **/swagger-ui/index.html** after starting the service.
