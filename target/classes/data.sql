INSERT INTO Brand(name)
SELECT N'Toyota'
WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'Toyota');

INSERT INTO Brand(name)
SELECT N'BMW'
WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'BMW');

INSERT INTO Brand(name)
SELECT N'Mercedes'
WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'Mercedes');

INSERT INTO Brand(name)
SELECT N'Honda'
WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE name = N'Honda');

INSERT INTO Car(name,price,image,description,brand_id,year,color)
SELECT N'Toyota Camry',1200000000,'camry.jpg',N'Sedan cao cấp',1,2023,N'Đen'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Toyota Camry');

INSERT INTO Car(name,price,image,description,brand_id,year,color)
SELECT N'BMW X5',3500000000,'x5.jpg',N'SUV sang trọng',2,2024,N'Trắng'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'BMW X5');

INSERT INTO Car(name,price,image,description,brand_id,year,color)
SELECT N'Mercedes C300',2500000000,'c300.jpg',N'Sedan Đức',3,2023,N'Xám'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Mercedes C300');

INSERT INTO Car(name,price,image,description,brand_id,year,color)
SELECT N'Honda Civic',900000000,'civic.jpg',N'Xe thể thao',4,2022,N'Đỏ'
WHERE NOT EXISTS (SELECT 1 FROM Car WHERE name = N'Honda Civic');

INSERT INTO Account(username,password,fullname,email,role)
SELECT 'admin','{noop}123',N'Admin','admin@gmail.com','ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM Account WHERE username='admin');

INSERT INTO Account(username,password,fullname,email,role)
SELECT 'user1','{noop}123',N'User','user@gmail.com','ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM Account WHERE username='user1');

