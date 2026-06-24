CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255),
    position_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'APPLIED',
    date_applied DATE,
    location VARCHAR(500),
    notes TEXT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_application FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)