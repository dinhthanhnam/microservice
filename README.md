# Microservices System Design – Đề 001

## Công nghệ

- Java 21
- Spring Boot `4.1.0`
- Spring Cloud `2025.1.2`
- Config Server (native), Eureka Discovery, Gateway + LoadBalancer, OpenFeign
- H2 file database; không cần cài MySQL

## Chạy hệ thống

Mở 5 terminal PowerShell, chạy đúng thứ tự:

```powershell
cd Config-Server; .\gradlew.bat bootRun
cd Discovery_Server; .\gradlew.bat bootRun
cd Api-Gateway; .\gradlew.bat bootRun
cd Product-Service; .\gradlew.bat bootRun
cd Order-Service; .\gradlew.bat bootRun
```

Chờ từng service khởi động xong. Kiểm tra config tập trung tại:

```text
http://localhost:8888/product-service/default
http://localhost:8888/order-service/default
```

Trong log product/order phải thấy `app.demo-message` được nạp từ config server. Port nội bộ lần lượt là `8081` và `8082`; client chỉ test qua gateway `8080`.

## API test qua gateway

Xem sản phẩm mẫu:

```powershell
curl.exe http://localhost:8080/api/products
```

### 1. Đặt hàng thành công

```powershell
curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d '{"productId":1,"quantity":2}'
```

Kết quả mong đợi: `201 Created`, `status=CREATED`.

### 2. Sản phẩm không tồn tại

```powershell
curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d '{"productId":9999,"quantity":1}'
```

Kết quả mong đợi: `404 Not Found`, không tạo order.

### 3. Tồn kho không đủ

```powershell
curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d '{"productId":2,"quantity":999}'
```

Kết quả mong đợi: `409 Conflict`, không tạo order.

## Mapping kiến trúc

- `product-service`: `GET/POST /api/products`, `GET /api/products/{id}`, endpoint kiểm tra tồn kho.
- `order-service`: `POST /api/orders`; Feign gọi `product-service` bằng `@FeignClient(name = "product-service")`.
- Gateway: `/api/products/** -> lb://product-service`, `/api/orders/** -> lb://order-service`.
- Local `application.yml` của hai service chỉ khai báo tên ứng dụng và `spring.config.import`; port, datasource, Eureka, Feign timeout nằm tại `Config-Server/config-repo`.
