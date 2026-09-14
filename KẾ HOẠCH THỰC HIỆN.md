# Kế hoạch làm bài Hackathon Microservices – Đề 001

## Mục tiêu

Hoàn thiện hệ thống tối thiểu gồm `config-server`, `discovery-server`, `api-gateway`, `product-service`, `order-service`; mọi API nghiệp vụ được gọi qua gateway ở cổng `8080`.

## Trình tự tối giản

1. Lấy base project, kiểm tra version Spring Boot/Spring Cloud và cấu trúc config repo.
2. Bổ sung cấu hình tập trung cho `product-service` và `order-service`: port, datasource, Eureka, Feign; local `application.yml` chỉ giữ `spring.application.name` và `spring.config.import`.
3. Bổ sung route gateway dùng `lb://product-service` và `lb://order-service`.
4. Tạo `product-service`: entity sản phẩm, JPA repository, API xem sản phẩm/kiểm tra tồn kho, dữ liệu mẫu.
5. Tạo `order-service`: entity order, JPA repository, DTO, Feign client gọi `product-service` theo service name, xử lý `404`/`409`, tạo order `201`.
6. Viết README với thứ tự chạy và 3 lệnh test qua cổng `8080`.
7. Build/test toàn bộ; nếu chạy được thì kiểm tra Eureka, gateway và ba kịch bản nghiệp vụ.

## Các điểm cần nhớ khi thi

- Local config tối giản: `spring.application.name` phải khớp tên file config; thêm `spring.config.import=configserver:http://localhost:8888`.
- Gateway route phải dùng `lb://<service-name>`, không dùng port/IP.
- Feign client phải dùng `@FeignClient(name = "product-service")`, không hard-code URL.
- Lỗi sản phẩm không tồn tại: `404`; tồn kho không đủ: `409`; không tạo order khi lỗi.
- Test luôn bằng `http://localhost:8080`, theo thứ tự: config → discovery → gateway → product/order.

## Tiêu chí hoàn tất

- Build không lỗi.
- Hai service có config tập trung và đăng ký Eureka.
- Gateway có đủ 2 route load-balanced.
- Thành công, product không tồn tại, và tồn kho không đủ đều có phản hồi đúng.
- Có `README.md` hướng dẫn chạy/test.
