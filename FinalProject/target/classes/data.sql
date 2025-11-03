-- Insert test user (password is 'password123' encoded with BCrypt)
INSERT INTO users (name, email, password, role)
VALUES ('Test User', 'user@example.com', '$2a$10$YCJ0oM5qL5PNGK.DuGGEi.jrLMTdWZZ5XY5WGQGqb1dY6pQmm5NZi', 'USER')
ON DUPLICATE KEY UPDATE id=id;

-- Insert test product
INSERT INTO products (name, description, price, stock_quantity, category)
VALUES ('Test Product', 'A test product', 99.99, 10, 'Electronics')
ON DUPLICATE KEY UPDATE id=id;
