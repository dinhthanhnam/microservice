# HACKATHON MÔN MICROSERVICES SYSTEM DESIGN \- ĐỀ 001

**HACKATHON MÔN MICROSERVICES SYSTEM DESIGN \- ĐỀ 001**

## **GIỮA MÔN**

## **MICROSERVICES SYSTEM DESIGN \- Đề 001**

## **THỜI GIAN: 90 phút**

**\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\***

---

## **Yêu cầu: **

- *Tạo github repository theo cú pháp :  ****\[Tên lớp\]\_\[Họ Tên\]\_\[Mã đề\]**** *

- *Ví dụ: ****HN\-K24\-CNTT1\_NguyenVanA\_001***

- *Sau khi hoàn thành, đẩy code lên github repo và nộp link cho người phụ trách*

---

## **I\. BỐI CẢNH ĐỀ THI**

Sinh viên được cung cấp một **base project** tên \`base\-project\-hackathon\` đã có sẵn 3 thành phần hạ tầng:
[Nhấn vào để lấy link base project](https://github.com/huongcaoha/base-project-hackathon) \.

|Service|Vai trò|
|---|---|
|`config-server`|Cung cấp cấu hình tập trung \(Spring Cloud Config\) cho toàn bộ hệ thống|
|`discovery-server`|Đăng ký \& khám phá dịch vụ \(Eureka Server\)|
|`api-gateway`|Cổng vào duy nhất của hệ thống, lắng nghe tại cổng **8080**|



**Nhiệm vụ:** Tải project về, tiếp tục phát triển thành một hệ thống thương mại điện tử tối thiểu bằng cách bổ sung thêm **2 service nghiệp vụ**:

- `product-service` — quản lý sản phẩm \& tồn kho

- `order-service` — xử lý đặt hàng, gọi sang `product-service` để xác thực trước khi tạo đơn

---



## **II\. YÊU CẦU KIẾN TRÚC BẮT BUỘC**

### 1\. **Tập trung cấu hình:** 

- Toàn bộ cấu hình \(port nội bộ, datasource, cấu hình Eureka, cấu hình Feign\.\.\.\) của `product-service` và `order-service` **phải lấy từ ****`config-server`**, không được hard\-code trong `application.yml` cục bộ của từng service\. Sinh viên **không cần tạo file ****`bootstrap.yml`** — chỉ cần tạo file `application.yml` tối giản, đặt đúng `spring.application.name` trùng với tên file cấu hình tương ứng trên config\-server và khai báo `spring.config.import` trỏ tới config\-server, hệ thống sẽ tự động lấy được cấu hình còn lại \(đây là cách làm chuẩn của các phiên bản Spring Cloud Config hiện nay\)\.

- **Mục đích: **khi cần thay đổi cấu hình \(ví dụ đổi thông tin DB, đổi timeout Feign\.\.\.\) chỉ sửa tại config repo, **không phải build lại/test lại từng service**\.

### 2\. **Một cổng vào duy nhất:**

- Client \(Postman/trình duyệt\) **chỉ được phép gọi vào cổng 8080 của ****`api-gateway`**\. Không được gọi trực tiếp đến port riêng của `product-service` hay `order-service` để lấy dữ liệu nghiệp vụ\.

### 3\. **Đăng ký Eureka \& Load Balancing:** 

- Cả 2 service mới đều phải đăng ký thành công vào `discovery-server`\. `api-gateway` phải định tuyến \(route\) đến các service thông qua tên service kết hợp với **load balancing** \(ví dụ dùng URI dạng `lb://product-service`, `lb://order-service` với Spring Cloud LoadBalancer\), tuyệt đối không dùng địa chỉ IP/port cứng\. Việc này nhằm đảm bảo khi một service được scale thành nhiều instance, gateway vẫn tự động phân tải được mà không cần cấu hình lại route\.

### 4\. **Giao tiếp liên service bằng OpenFeign:** 

- `order-service` phải gọi sang `product-service` thông qua **OpenFeign Client** \(không dùng RestTemplate/WebClient thuần\)\.

---



## **III\. LUỒNG NGHIỆP VỤ CẦN CÀI ĐẶT**

### **Chức năng: Đặt hàng \(Create Order\)**

```Java
Client
  │  POST /api/orders  (qua cổng 8080)
  ▼
api-gateway
  │  route "/api/orders/**" -> order-service
  ▼
order-service
  │  gọi Feign Client -> product-service: kiểm tra
  │     (1) sản phẩm có tồn tại không?
  │     (2) số lượng tồn kho có đủ so với số lượng đặt không?
  ▼
product-service
  │  trả về thông tin sản phẩm / tồn kho hoặc lỗi "not found"
  ▼
order-service
  ├─ Nếu hợp lệ  -> tạo Order (status = CREATED/PENDING), trả về 201 + thông tin đơn hàng
  └─ Nếu không hợp lệ -> trả về lỗi rõ ràng (404 nếu sản phẩm không tồn tại, 400/409 nếu không đủ tồn kho)
  ▼
Client nhận phản hồi cuối cùng từ api-gateway
```

---



## **IV\. ĐỀ BÀI CHI TIẾT \(Các câu thực hành\)**



*Lưu ý: Đề không cung cấp sẵn cấu trúc entity/API cụ thể\. Sinh viên tự thiết kế model dữ liệu, endpoint và mã lỗi phù hợp, miễn đáp ứng đúng luồng nghiệp vụ và các ràng buộc kiến trúc ở mục II, III\.*

### **Câu 1 \(20 điểm\) — Cấu hình tập trung**

Tạo cấu hình cho `product-service` và `order-service` trên `config-server` \(native/git repo tuỳ base project đã cấu hình sẵn\)\. Chứng minh rằng khi service khởi động, nó lấy cấu hình \(ví dụ `server.port`, tên datasource, hoặc một property tuỳ chỉnh do bạn tự đặt\) từ `config-server` chứ không phải từ file cấu hình local, bằng cách:

- Thay đổi 1 giá trị cấu hình trên config\-server\.

- Refresh hoặc khởi động lại service và chụp lại log/kết quả chứng minh giá trị mới được áp dụng\.

### **Câu 2 \(20 điểm\) — Đăng ký \& Định tuyến qua Gateway kèm Load Balancing**

- Cấu hình `product-service` và `order-service` đăng ký vào `discovery-server`\.

- Cấu hình route trên `api-gateway` sao cho:

    - `http://localhost:8080/api/products/**` → `product-service`

    - `http://localhost:8080/api/orders/**` → `order-service`

\- Route phải trỏ đến service thông qua **load balancer** \(URI dạng \`lb://\<tên\-service\>\`\), không dùng địa chỉ/port cố định\.

- Chứng minh việc gọi trực tiếp vào port riêng của `product-service`/`order-service` \(nếu có thể\) không phải là đường đi hợp lệ theo thiết kế — toàn bộ test case phải thực hiện qua cổng 8080\.

### **Câu 3 \(25 điểm\) — Cài đặt product\-service**

- Tự thiết kế entity, repository, service, controller phù hợp để quản lý thông tin sản phẩm và số lượng tồn kho\.

- Tự chuẩn bị dữ liệu mẫu sao cho đủ để phục vụ đầy đủ các kịch bản kiểm thử ở Câu 5 \(bao gồm cả trường hợp sản phẩm không tồn tại và trường hợp tồn kho không đủ\)\.

### **Câu 4 \(25 điểm\) — Cài đặt order\-service \+ gọi Feign sang product\-service**

- Khai báo `@FeignClient` gọi sang `product-service` \(qua tên service, không hard\-code URL\)\.

- Cài đặt API tạo đơn hàng theo đúng luồng nghiệp vụ ở mục III:

    - Sản phẩm không tồn tại → trả lỗi phù hợp \(không tạo order\)\.

    - Số lượng đặt \> tồn kho → trả lỗi phù hợp \(không tạo order\)\.

    - Hợp lệ → tạo order thành công, trả về thông tin order\.

### **Câu 5 \(10 điểm\) — Kiểm thử tổng thể**

Dùng Postman/curl gọi qua **cổng 8080** để minh chứng đủ 3 kịch bản:

- Đặt hàng thành công\.

- Đặt hàng với `productId` không tồn tại\.

- Đặt hàng với số lượng vượt quá tồn kho\.

Nộp kèm ảnh chụp/collection Postman của cả 3 kịch bản trên\.



---



## **V\. YÊU CẦU NỘP BÀI**

- Đóng gói toàn bộ các dự án vào chung một folder , hình ảnh và các file kèm theo đẩy lên github sau đó dán link vào phần nộp bài trên portal \.

- Kèm file `README.md` ngắn gọn nêu: cách chạy hệ thống theo đúng thứ tự  và danh sách API test qua cổng 8080 \.

---

## **VI\. THANG ĐIỂM TỔNG HỢP**

|**Tiêu chí**|**Điểm**|
|---|---|
|Cấu hình tập trung qua config\-server đúng \& chứng minh được|20|
|Đăng ký Eureka \+ định tuyến Gateway đúng, chỉ truy cập qua 8080|20|
|Cài đặt product\-service đầy đủ|25|
|Cài đặt order\-service \+ Feign \+ xử lý luồng nghiệp vụ đúng|25|
|Kiểm thử đủ 3 kịch bản qua Postman|10|
|Tổng điểm|100|



