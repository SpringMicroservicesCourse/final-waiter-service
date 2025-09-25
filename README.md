# SpringBucks 服務員微服務 ⚡

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.2-blue.svg)](https://spring.io/projects/spring-cloud)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue.svg)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 專案介紹

本專案為 SpringBucks 咖啡店系統的服務員微服務，負責處理咖啡產品管理、訂單處理、以及與其他微服務的整合。此服務整合了服務鏈路追蹤、服務發現、快取機制等現代微服務架構的核心功能，並支援 Docker 容器化部署。

**核心功能：**
- **咖啡產品管理**：新增、查詢、批量匯入咖啡產品資訊
- **訂單處理**：建立與管理咖啡訂單，支援多種咖啡組合
- **服務註冊與發現**：自動向 Consul 服務註冊中心註冊服務實例
- **鏈路追蹤**：整合 Zipkin 進行分散式鏈路追蹤
- **快取機制**：使用 Redis 提供高效能的資料快取服務
- **健康監控**：整合 Actuator 提供完整的服務健康檢查與監控指標

> 💡 **為什麼選擇此微服務架構？**
> - 完整的微服務生態系統整合，包含服務發現、鏈路追蹤、快取等
> - 支援 Docker 容器化部署，便於 DevOps 實踐
> - 採用領域驅動設計（DDD）原則，業務邏輯清晰分離
> - 整合現代化監控與可觀測性工具

### 🎯 專案特色

- **微服務架構**：完整的服務註冊與發現機制，支援 Consul 整合
- **鏈路追蹤**：整合 Zipkin 進行分散式系統的請求追蹤
- **高效能快取**：使用 Redis 實現多層快取策略，大幅提升查詢效能
- **容器化部署**：支援 Docker 打包與部署，便於環境一致性
- **精確金額處理**：使用 Joda Money 確保財務計算的準確性
- **監控整合**：支援 Prometheus 指標收集與健康檢查

## 技術棧

### 核心框架
- **Spring Boot 3.4.5** - 主框架，提供自動配置與生產就緒功能
- **Spring Cloud 2024.0.2** - 微服務框架，提供服務註冊與發現等功能
- **Spring Data JPA** - 資料持久層框架，簡化資料庫操作
- **Spring Cache** - 宣告式快取抽象層
- **Spring Data Redis** - Redis 整合與操作框架

### 微服務與監控
- **Consul** - 服務註冊與發現中心
- **Zipkin** - 分散式鏈路追蹤系統
- **Micrometer** - 應用程式指標收集
- **Spring Cloud Stream** - 訊息驅動微服務框架

### 資料庫與快取
- **MariaDB** - 主要資料庫
- **Redis** - 快取與會話儲存
- **RabbitMQ** - 訊息佇列系統

### 開發工具與輔助
- **Lombok** - 減少樣板程式碼
- **Joda Money** - 專業的金錢計算和型態轉換
- **Docker** - 容器化部署
- **Maven** - 專案建構與依賴管理

## 專案結構

```
final-waiter-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tw/fengqing/spring/springbucks/waiter/
│   │   │       ├── WaiterServiceApplication.java          # 主要應用程式入口
│   │   │       ├── controller/                           # 控制器層
│   │   │       │   ├── CoffeeController.java            # 咖啡產品控制器
│   │   │       │   └── OrderController.java             # 訂單控制器
│   │   │       ├── model/                               # 資料模型
│   │   │       │   ├── Coffee.java                     # 咖啡實體
│   │   │       │   ├── CoffeeOrder.java                # 咖啡訂單實體
│   │   │       │   └── OrderState.java                 # 訂單狀態枚舉
│   │   │       ├── repository/                         # 資料存取層
│   │   │       │   ├── CoffeeRepository.java           # 咖啡資料存取
│   │   │       │   └── CoffeeOrderRepository.java      # 訂單資料存取
│   │   │       ├── service/                            # 業務邏輯層
│   │   │       │   ├── CoffeeService.java              # 咖啡業務邏輯
│   │   │       │   └── CoffeeOrderService.java        # 訂單業務邏輯
│   │   │       ├── integration/                       # 整合層
│   │   │       │   ├── Barista.java                   # 咖啡師服務整合
│   │   │       │   └── Customer.java                  # 客戶服務整合
│   │   │       └── controller/                        # 效能監控
│   │   │           └── PerformanceInteceptor.java    # 效能攔截器
│   │   └── resources/
│   │       ├── application.properties                  # 應用程式配置
│   │       └── bootstrap.properties                    # 啟動配置
│   └── test/
├── Dockerfile                                          # Docker 建構檔案
├── pom.xml                                            # Maven 專案配置
└── README.md                                          # 專案說明文件
```

## 快速開始

### 前置需求
- **Java 21** - 最新 LTS 版本的 Java
- **Maven 3.6+** - 專案建構工具
- **Docker** - 容器化部署（選用）
- **MariaDB** - 資料庫（或使用 Docker 容器）
- **Redis** - 快取服務（或使用 Docker 容器）
- **Consul** - 服務註冊中心（或使用 Docker 容器）

### 安裝與執行

1. **克隆此倉庫：**
```bash
git clone https://github.com/username/springbucks-microservices.git
```

2. **進入專案目錄：**
```bash
cd Chapter\ 16\ 服務鏈路追蹤/final-waiter-service
```

3. **編譯專案：**
```bash
mvn clean compile
```

4. **執行應用程式：**
```bash
mvn spring-boot:run
```

### Docker 部署

1. **建構 Docker 映像檔：**
```bash
mvn clean package dockerfile:build
```

2. **執行 Docker 容器：**
```bash
docker run -p 8080:8080 springbucks/final-waiter-service:0.0.1-SNAPSHOT
```

3. **使用 Docker Compose 啟動完整環境：**
```bash
cd Chapter\ 16\ 服務鏈路追蹤
docker-compose up -d
```

## 進階說明

### 環境變數
```properties
# 資料庫配置
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/springbucks
SPRING_DATASOURCE_USERNAME=springbucks
SPRING_DATASOURCE_PASSWORD=springbucks

# Redis 配置
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Consul 配置
SPRING_CLOUD_CONSUL_HOST=localhost
SPRING_CLOUD_CONSUL_PORT=8500

# Zipkin 配置
MANAGEMENT_TRACING_ENDPOINT=http://localhost:9411/api/v2/spans
```

### 設定檔說明
```properties
# application.properties 主要設定
spring.application.name=waiter-service
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.jpa.hibernate.ddl-auto=update

# Redis 快取配置
spring.cache.type=redis
spring.cache.redis.time-to-live=600000

# 服務發現配置
spring.cloud.consul.discovery.service-name=${spring.application.name}
spring.cloud.consul.discovery.health-check-interval=10s
```

## API 端點

### 咖啡產品管理
- `GET /coffee` - 查詢所有咖啡產品
- `GET /coffee/{id}` - 查詢特定咖啡產品
- `POST /coffee` - 新增咖啡產品
- `PUT /coffee/{id}` - 更新咖啡產品

### 訂單管理
- `GET /order` - 查詢所有訂單
- `GET /order/{id}` - 查詢特定訂單
- `POST /order` - 建立新訂單
- `PUT /order/{id}/state` - 更新訂單狀態

### 監控端點
- `GET /actuator/health` - 健康檢查
- `GET /actuator/metrics` - 應用程式指標
- `GET /actuator/prometheus` - Prometheus 指標

## 參考資源

- [Spring Boot 官方文件](https://spring.io/projects/spring-boot)
- [Spring Cloud 官方文件](https://spring.io/projects/spring-cloud)
- [Consul 官方文件](https://www.consul.io/docs)
- [Zipkin 官方文件](https://zipkin.io/)
- [Redis 官方文件](https://redis.io/docs)

## 注意事項與最佳實踐

### ⚠️ 重要提醒

| 項目 | 說明 | 建議做法 |
|------|------|----------|
| 資料庫連線 | 使用連線池管理 | 設定適當的連線池大小 |
| 快取策略 | Redis 快取管理 | 設定合理的 TTL 時間 |
| 服務發現 | Consul 健康檢查 | 定期檢查服務健康狀態 |
| 鏈路追蹤 | Zipkin 採樣率 | 生產環境調整採樣率以降低效能影響 |

### 🔒 最佳實踐指南

- **效能優化**：使用 Redis 快取提升查詢效能，設定適當的快取策略
- **監控告警**：整合 Prometheus 和 Grafana 進行系統監控
- **錯誤處理**：實作完整的例外處理機制，提供友善的錯誤訊息
- **安全性**：使用環境變數管理敏感資訊，避免硬編碼
- **容器化**：使用 Docker 確保環境一致性，便於部署和擴展

## 授權說明

本專案採用 MIT 授權條款，詳見 LICENSE 檔案。

## 關於我們

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。

## 聯繫我們

- **FB 粉絲頁**：[風清雲談 | Facebook](https://www.facebook.com/profile.php?id=61576838896062)
- **LinkedIn**：[linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- **YouTube 頻道**：[雲談風清 - YouTube](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- **風清雲談 部落格**：[風清雲談](https://blog.fengqing.tw/)
- **電子郵件**：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**📅 最後更新：2025-01-27**  
**👨‍💻 維護者：風清雲談團隊**
