CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255),
    temp_password VARCHAR(255),
    email_id VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    permissions VARCHAR(500) NOT NULL,
    first_login BOOLEAN DEFAULT TRUE
);

--CREATE TABLE IF NOT EXISTS user_roles (
--    user_id BIGINT NOT NULL,
--    roles VARCHAR(50) NOT NULL,
--
--    CONSTRAINT fk_user_roles_user
--        FOREIGN KEY (user_id)
--        REFERENCES users(id)
--        ON DELETE CASCADE
--);
