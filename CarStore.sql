CREATE DATABASE CarStore
GO

USE CarStore
GO

CREATE TABLE Brand(
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(50)
);

CREATE TABLE Car(
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100),
    price FLOAT,
    image NVARCHAR(255),
    description NVARCHAR(MAX),
    brand_id INT,
    year INT,
    color NVARCHAR(50),
    FOREIGN KEY (brand_id) REFERENCES Brand(id)
);

CREATE TABLE Account(
    username NVARCHAR(50) PRIMARY KEY,
    password NVARCHAR(100),
    fullname NVARCHAR(100),
    email NVARCHAR(100),
    role NVARCHAR(20)
);

CREATE TABLE Orders(
    id INT IDENTITY PRIMARY KEY,
    username NVARCHAR(50),
    create_date DATETIME DEFAULT GETDATE(),
    address NVARCHAR(255),
    status NVARCHAR(50)
);

CREATE TABLE OrderDetail(
    id INT IDENTITY PRIMARY KEY,
    order_id INT,
    car_id INT,
    price FLOAT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES Orders(id),
    FOREIGN KEY (car_id) REFERENCES Car(id)
);

-- DATA MẪU
INSERT INTO Brand(name) VALUES ('Toyota'),('BMW'),('Mercedes'),('Honda');

INSERT INTO Car(name,price,image,description,brand_id,year,color)
VALUES
('Toyota Camry',1200000000,'camry.jpg','Sedan cao cấp',1,2023,'Đen'),
('BMW X5',3500000000,'x5.jpg','SUV sang trọng',2,2024,'Trắng'),
('Mercedes C300',2500000000,'c300.jpg','Sedan Đức',3,2023,'Xám'),
('Honda Civic',900000000,'civic.jpg','Xe thể thao',4,2022,'Đỏ');

INSERT INTO Account VALUES
('admin','{noop}123','Admin','admin@gmail.com','ROLE_ADMIN'),
('user1','{noop}123','User','user@gmail.com','ROLE_USER');