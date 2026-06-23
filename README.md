# CarStore - Ứng dụng bán xe ô tô

## Mô tả dự án
Ứng dụng Spring Boot quản lý bán xe ô tô với các tính năng:
- Quản lý sản phẩm (xe, hãng sản xuất)
- Giỏ hàng và đặt hàng
- Quản lý đơn hàng
- Hệ thống hỗ trợ khách hàng
- Bảng điều khiển quản trị
- Xác thực người dùng

## Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- SQL Server hoặc H2 (để test)

## Hướng dẫn chạy

### 1. Test không cần SQL Server (sử dụng H2)
```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"
```
- Ứng dụng chạy tại: `http://localhost:8091`
- Dữ liệu test được tạo tự động

### 2. Chạy với SQL Server (production)
1. Chuẩn bị SQL Server:
   - Tạo database `CarStore`
   - Chạy file `CarStore.sql` ở root project

2. Cập nhật `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://YOUR_SERVER:1433;databaseName=carstore
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
```

3. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

## Tài khoản Test

| Tài khoản | Mật khẩu | Vai trò |
|-----------|----------|---------|
| admin | 123 | Quản trị viên |
| user1 | 123 | Khách hàng |
| user2 | 123 | Khách hàng |
| user3 | 123 | Khách hàng |

## Tính năng chính

### Khách hàng
- Xem danh sách xe: `/`
- Xem chi tiết xe: `/car/detail/{id}`
- Giỏ hàng: `/cart/view`
- Đặt hàng: `/order/checkout`
- Xem lịch sử đơn hàng: `/order/my-orders`
- Hỗ trợ khách hàng: `/support`
- Cài đặt tài khoản: `/profile`

### Quản trị viên
- Dashboard: `/admin` (thống kê, quản lý)
- Quản lý xe: `/car/list`, `/car/create`, `/car/edit`
- Quản lý tài khoản: `/admin/users`
- Quản lý đơn hàng: `/admin/orders`

## REST API (Chính)

Tất cả API endpoints bắt đầu từ `/api/`:

| Endpoint | Mô tả |
|----------|------|
| `GET /cars` | Danh sách xe |
| `GET /brands` | Danh sách hãng |
| `POST /auth/login` | Đăng nhập |
| `POST /auth/signup` | Đăng ký |
| `GET /profile` | Thông tin cá nhân |
| `GET /cart` | Giỏ hàng |
| `POST /cart/add/{id}?quantity=1` | Thêm vào giỏ |
| `POST /orders/checkout` | Đặt hàng |
| `GET /orders` | Danh sách đơn hàng |
| `GET /admin/stats` | Thống kê (Admin) |

## Cấu trúc dự án

```
src/main/
├── java/com/example/carstore/
│   ├── controller/          (REST + Web UI controllers)
│   ├── entity/              (Model classes)
│   ├── repository/          (Database access)
│   ├── service/             (Business logic)
│   └── config/              (Security, configuration)
├── resources/
│   ├── application.properties      (SQL Server)
│   ├── application-h2.properties   (H2)
│   ├── data.sql                    (Initial data)
│   ├── templates/                  (Thymeleaf HTML)
│   └── static/                     (CSS, images)
```

## Công nghệ sử dụng
- Spring Boot 2.7.18
- Spring Data JPA + Hibernate
- Spring Security + OAuth2
- Thymeleaf
- Bootstrap 5
- H2 Database / SQL Server

---
**Dự án tốt nghiệp - FPT Polytechnic**

