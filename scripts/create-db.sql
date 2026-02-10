-- Run this in PostgreSQL (psql or any client) before starting the app.
-- Creates the database used by app-runner and standalone services (monolith mode).

CREATE DATABASE inxinfo;

-- Optional: create separate DBs for running services in distributed mode:
-- CREATE DATABASE auth_db;
-- CREATE DATABASE puja_db;
-- CREATE DATABASE pandit_db;
-- CREATE DATABASE order_db;
-- CREATE DATABASE payment_db;
