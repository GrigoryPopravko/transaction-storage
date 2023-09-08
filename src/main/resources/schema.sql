CREATE TABLE IF NOT EXISTS transaction
(
    id        INT PRIMARY KEY,
    timestamp TIMESTAMP(6)   NOT NULL,
    type      VARCHAR(50) NOT NULL,
    actor     VARCHAR(50) NOT NULL,
    INDEX idx_transaction_timestamp (timestamp)
);

CREATE TABLE IF NOT EXISTS transaction_data
(
    transaction_id INT REFERENCES transaction (id) ON DELETE CASCADE,
    tag            VARCHAR(50) NOT NULL,
    value          VARCHAR(50) NOT NULL
);
