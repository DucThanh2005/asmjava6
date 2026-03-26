-- ===============================================
-- CARSTORE DATABASE - FULL SETUP
-- Chạy trên SQL Server
-- ===============================================

-- ===== 1. TẠO DATABASE =====
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'CarStore')
BEGIN
    ALTER DATABASE CarStore SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE CarStore;
END
GO

CREATE DATABASE CarStore;
GO

USE CarStore;
GO

-- ===== 2. TẠO CÁC BẢNG BỀN =====

CREATE TABLE Brand (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Car (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    price FLOAT NOT NULL,
    image NVARCHAR(255),
    description NVARCHAR(MAX),
    brand_id INT NOT NULL,
    year INT,
    color NVARCHAR(50),
    FOREIGN KEY (brand_id) REFERENCES Brand(id)
);

CREATE TABLE Account (
    username NVARCHAR(50) PRIMARY KEY,
    password NVARCHAR(100) NOT NULL,
    fullname NVARCHAR(100),
    email NVARCHAR(100),
    role NVARCHAR(20) NOT NULL
);

CREATE TABLE Orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL,
    create_date DATETIME DEFAULT GETDATE(),
    address NVARCHAR(255),
    status NVARCHAR(50),
    FOREIGN KEY (username) REFERENCES Account(username)
);

CREATE TABLE OrderDetail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    car_id INT NOT NULL,
    price FLOAT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id),
    FOREIGN KEY (car_id) REFERENCES Car(id)
);

-- ===== 3. NHẬP DỮ LIỆU BRAND (HÃN) =====
INSERT INTO Brand(name) VALUES 
(N'Toyota'),
(N'BMW'),
(N'Mercedes'),
(N'Honda');

-- ===== 4. NHẬP DỮ LIỆU CAR (XE) =====
INSERT INTO Car(name, price, image, description, brand_id, year, color) VALUES
(N'Toyota Camry', 1200000000, 'camry.jpg', N'Sedan cao cấp, an toàn, tiết kiệm xăng', 1, 2023, N'Đen'),
(N'BMW X5', 3500000000, 'x5.jpg', N'SUV sang trọng, động cơ mạnh, nội thất cao cấp', 2, 2024, N'Trắng'),
(N'Mercedes C300', 2500000000, 'c300.jpg', N'Sedan Đức, công nghệ mới, lái tự động', 3, 2023, N'Xám'),
(N'Honda Civic', 900000000, 'civic.jpg', N'Xe thể thao, thiết kế năng động, tiết kiệm', 4, 2022, N'Đỏ'),
(N'Toyota Corolla', 800000000, 'corolla.jpg', N'Sedan nhỏ gọn, tin cậy, bảo dưỡng rẻ', 1, 2023, N'Bạc'),
(N'BMW 3 Series', 2000000000, 'bmw3.jpg', N'Sedan thể thao, hiệu năng cao, lái cảm giác tuyệt vời', 2, 2024, N'Xanh đen');

-- ===== 5. NHẬP DỮ LIỆU ACCOUNT (TÀI KHOẢN) =====
INSERT INTO Account(username, password, fullname, email, role) VALUES
('admin', '{noop}123', N'Quản trị viên', 'admin@carstore.com', 'ROLE_ADMIN'),
('user1', '{noop}123', N'Nguyễn Văn A', 'user1@carstore.com', 'ROLE_USER'),
('user2', '{noop}123', N'Trần Thị B', 'user2@carstore.com', 'ROLE_USER'),
('user3', '{noop}123', N'Lê Văn C', 'user3@carstore.com', 'ROLE_USER');

-- ===== 6. KIỂM TRA DỮ LIỆU =====
PRINT '========================================';
PRINT 'KIỂM TRA DỮ LIỆU ĐÃ NHẬP';
PRINT '========================================';

PRINT '--- Brand (Hãng) ---';
SELECT * FROM Brand;

PRINT '--- Car (Xe) ---';
SELECT * FROM Car;

PRINT '--- Account (Tài khoản) ---';
SELECT * FROM Account;

PRINT '--- Tóm tắt ---';
SELECT 
    (SELECT COUNT(*) FROM Brand) AS [Số Hãng],
    (SELECT COUNT(*) FROM Car) AS [Số Xe],
    (SELECT COUNT(*) FROM Account) AS [Số Tài khoản];

GO

PRINT 'Setup hoàn thành! ✓';