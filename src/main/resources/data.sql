-- ===============================================
-- DATA SQL - Spring Boot tự động load
-- ===============================================

-- ===== NHẬP DỮ LIỆU BRAND (HÃN) =====
INSERT INTO Brand(name) 
SELECT N'Toyota' WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'Toyota');

INSERT INTO Brand(name) 
SELECT N'BMW' WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'BMW');

INSERT INTO Brand(name) 
SELECT N'Mercedes' WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'Mercedes');

INSERT INTO Brand(name) 
SELECT N'Honda' WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'Honda');

-- ===== NHẬP DỮ LIỆU CAR (XE) =====
INSERT INTO Car(name, price, image, description, brand_id, year, color)
SELECT N'Toyota Camry', 1200000000, 'camry.jpg', N'Sedan cao cấp, an toàn, tiết kiệm xăng', 1, 2023, N'Đen'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Toyota Camry');

INSERT INTO Car(name, price, image, description, brand_id, year, color)
SELECT N'BMW X5', 3500000000, 'x5.jpg', N'SUV sang trọng, động cơ mạnh, nội thất cao cấp', 2, 2024, N'Trắng'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'BMW X5');

INSERT INTO Car(name, price, image, description, brand_id, year, color)
SELECT N'Mercedes C300', 2500000000, 'c300.jpg', N'Sedan Đức, công nghệ mới, lái tự động', 3, 2023, N'Xám'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Mercedes C300');

INSERT INTO Car(name, price, image, description, brand_id, year, color)
SELECT N'Honda Civic', 900000000, 'civic.jpg', N'Xe thể thao, thiết kế năng động, tiết kiệm', 4, 2022, N'Đỏ'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Honda Civic');

INSERT INTO Car(name, price, image, description, brand_id, year, color)
SELECT N'Toyota Corolla', 800000000, 'corolla.jpg', N'Sedan nhỏ gọn, tin cậy, bảo dưỡng rẻ', 1, 2023, N'Bạc'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Toyota Corolla');

INSERT INTO Car(name, price, image, description, brand_id, year, color)
SELECT N'BMW 3 Series', 2000000000, 'bmw3.jpg', N'Sedan thể thao, hiệu năng cao, lái cảm giác tuyệt vời', 2, 2024, N'Xanh đen'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'BMW 3 Series');

-- ===== NHẬP DỮ LIỆU ACCOUNT (TÀI KHOẢN) =====
INSERT INTO Account(username, password, fullname, email, role)
SELECT 'admin', '{noop}123', N'Quản trị viên', 'admin@carstore.com', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM Account WHERE username = 'admin');

INSERT INTO Account(username, password, fullname, email, role)
SELECT 'user1', '{noop}123', N'Nguyễn Văn A', 'user1@carstore.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM Account WHERE username = 'user1');

INSERT INTO Account(username, password, fullname, email, role)
SELECT 'user2', '{noop}123', N'Trần Thị B', 'user2@carstore.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM Account WHERE username = 'user2');

INSERT INTO Account(username, password, fullname, email, role)
SELECT 'user3', '{noop}123', N'Lê Văn C', 'user3@carstore.com', 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM Account WHERE username = 'user3');

