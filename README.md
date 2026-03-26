# CarStore (Spring Boot + SQL Server)

## Chạy project

1. **Chuẩn bị SQL Server**
   - Tạo database `CarStore` (hoặc chạy file `CarStore.sql` ở root project).
   - Cập nhật cấu hình trong `src/main/resources/application.properties` nếu máy bạn khác:
     - `spring.datasource.url`
     - `spring.datasource.username`
     - `spring.datasource.password`

2. **Chạy ứng dụng**

```bash
mvn spring-boot:run
```

3. **Truy cập**
   - App chạy ở: `http://localhost:8082/`

## Tài khoản đăng nhập (lấy từ SQL)

- **Admin**: `admin` / `123`
- **User**: `user1` / `123`

> Dữ liệu mẫu (Brand/Car/Account) được nạp qua `src/main/resources/data.sql` nếu chưa có.

## Chức năng chính

- Trang chủ hiển thị danh sách xe (lấy từ SQL): `/`
- Quản lý xe (Thymeleaf):
  - Danh sách: `/car/list`
  - Thêm: `/car/create`
  - Sửa: `/car/edit/{id}`
  - Xóa: `/car/delete/{id}`
- Admin dashboard: `/admin/dashboard` (yêu cầu role `ROLE_ADMIN`)
- REST API xe: `/api/cars` (yêu cầu role `ROLE_ADMIN` cho CRUD theo cấu hình security)

