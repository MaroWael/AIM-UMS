-- Payment System Schema for Level Fees (No Semester Tracking)
-- Add this to your existing database

-- Payments Table for Level Fees
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    level INT NOT NULL,  -- Student level: 1, 2, 3, or 4
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EGP',
    description VARCHAR(255),
    payment_method VARCHAR(50) NOT NULL,  -- CARD, BANK_TRANSFER, CASH
    transaction_id VARCHAR(100) UNIQUE,
    status VARCHAR(20) NOT NULL,  -- SUCCESS, FAILED, PENDING, REFUNDED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_level CHECK (level BETWEEN 1 AND 4),
    CONSTRAINT chk_status CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING', 'REFUNDED')),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CARD', 'BANK_TRANSFER', 'CASH'))
);

-- Indexes for faster queries
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_level ON payments(level);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);

-- Composite index for checking if user paid for specific level
CREATE INDEX idx_payments_user_level ON payments(user_id, level, status);

