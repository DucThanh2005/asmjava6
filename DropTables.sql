-- ===============================================
-- XOÁ DATABASE CARSTORE
-- ===============================================

USE master;
GO

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'CarStore')
BEGIN
    PRINT 'Đang xoá database CarStore...';
    ALTER DATABASE CarStore SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE CarStore;
    PRINT 'Xoá database CarStore thành công! ✓';
END
ELSE
BEGIN
    PRINT 'Database CarStore không tồn tại.';
END
GO