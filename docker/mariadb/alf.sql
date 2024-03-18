-- MariaDB script

-- Drop the database if it exists
DROP DATABASE IF EXISTS alf;

-- Create the database
CREATE DATABASE alf;

-- Use the database
USE alf;

DROP TABLE IF EXISTS events;

CREATE TABLE events (
    id int(11) NOT NULL AUTO_INCREMENT,
    event_name VARCHAR(160) NOT NULL,
    created_at VARCHAR(160) NOT NULL,
    start_at VARCHAR(160) NOT NULL,
    location VARCHAR(160) NOT NULL,
    venue VARCHAR(160) NOT NULL,
    participants INT,
    completed BOOLEAN DEFAULT false,
    PRIMARY KEY (id)
);
