# 🚀 Stock Service V2 - Redis 기반 재고 관리 시스템 구현

## 📋 개요

기존 JPA 기반 재고 관리 시스템에 Redis를 활용한 고성능 V2 버전을 추가로 구현했습니다. V1과 V2를 병행 운영하여 성능과 안정성을 모두 확보할 수 있습니다.

## 🎯 주요 변경사항

### 1. 새로운 API 엔드포인트 추가

#### V2 API (Redis 기반)
```http
POST /api/stocks/v2
GET /api/stocks/v2/{stockId}
GET /api/stocks/v2/{stockId}/quantity
PATCH /api/stocks/v2/{stockId}/increase
PATCH /api/stocks/v2/{stockId}/decrease
```

#### V1 API (JPA 기반) - 기존 유지
```http
PATCH /api/stocks/v1/{stockId}/increase
PATCH /api/stocks/v1/{stockId}/decrease
```

### 2. 서비스 레이어 확장

#### StockService에 V2 메서드 추가
```java
public Stock registerV2(String productId, Long quantity)
public Stock getStockV2(String stockId)
public Long checkQuantityV2(String stockId)
public Long increaseQuantityV2(String stockId, Long quantity)
public Long decreaseQuantityV2(String stockId, Long quantity)
```

#### 특징
- **V1**: JPA를 사용한 동시성 안전한 재고 관리
- **V2**: Redis를 사용한 고성능 인메모리 재고 관리
- **공통**: 동일한 비즈니스 로직 검증 (null 체크, 음수 체크 등)

### 3. Repository Adapter 활용

#### 기존 구조 활용
- `StockJpaRepositoryAdapter`: V1에서 사용 (JPA)
- `StockRedisRepositoryAdapter`: V2에서 사용 (Redis)

#### 헥사고날 아키텍처 준수
- `StockRepository` 인터페이스를 통한 추상화
- 각 Adapter가 동일한 인터페이스 구현
- 의존성 역전 원칙 적용

## 🔧 기술적 구현 세부사항

### 1. Redis Repository Adapter
```java
@Component
public class StockRedisRepositoryAdapter implements StockRepository {
    // Redis의 원자적 연산 활용
    // INCRBY, DECRBY 명령어로 동시성 보장
    // TTL 설정으로 메모리 관리
}
```

### 2. 검증 로직 개선
```java
// StockUpdateRequest 검증 수정
@Min(value = 0, message = "수량은 0 이상이어야 합니다.")
private Long quantity;
```

### 3. 예외 처리 강화
```java
// GlobalExceptionHandler에 JSON 파싱 예외 처리 추가
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException()
```

### 4. Redis 전용 응답 DTO
```java
// StockRedisResponse - Redis 조회 전용 응답
public class StockRedisResponse {
    private String stockId;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // productId 제외 (Redis에서는 수량 정보만 저장)
}
```

### 5. 도메인 모델 개선
```java
// Stock.fromRedis() 메서드 개선
public static Stock fromRedis(String stockId, Long quantity) {
    // productId는 null로 설정 (Redis에서는 수량만 저장)
    // 성능 최적화를 위해 불필요한 정보 제외
}
```

## 🧪 테스트 커버리지

### 1. 단위 테스트
- `StockServiceV1Test`: JPA 기반 V1 메서드 테스트
- `StockServiceV2Test`: Redis 기반 V2 메서드 테스트

### 2. 통합 테스트
- `StockApiV1Test`: V1 API 엔드포인트 테스트
- `StockApiV2Test`: V2 API 엔드포인트 테스트

### 3. 검증 테스트
- `StockUpdateRequestTest`: DTO 검증 로직 테스트

### 4. 테스트 시나리오
- ✅ 성공 케이스
- ✅ 입력값 검증 (null, 빈 문자열, 음수)
- ✅ 재고 없음 에러
- ✅ 수량 부족 에러
- ✅ 경계값 테스트 (0, 음수)
- ✅ 연속 작업 테스트
- ✅ JSON 파싱 에러 처리

## 📊 성능 비교

### V1 (JPA) vs V2 (Redis)

| 항목 | V1 (JPA) | V2 (Redis) |
|------|----------|------------|
| **성능** | 보통 | 빠름 |
| **동시성** | 데이터베이스 레벨 보장 | Redis 원자적 연산 |
| **지속성** | 영구 저장 | TTL 기반 임시 저장 |
| **메모리 사용** | 낮음 | 높음 |
| **복잡도** | 도메인 로직 포함 | 단순 연산 |

## 🚀 사용 방법

### 1. V1 API 사용 (안정성 우선)
```bash
# 재고 증가
PATCH /api/stocks/v1/stock-123/increase
{
  "quantity": 50
}

# 재고 감소
PATCH /api/stocks/v1/stock-123/decrease
{
  "quantity": 30
}
```

### 2. V2 API 사용 (성능 우선)
```bash
# 재고 등록 (Redis)
POST /api/stocks/v2
{
  "productId": "product-001",
  "quantity": 100
}

# 재고 조회 (Redis)
GET /api/stocks/v2/stock-123

# 재고 수량 조회 (Redis)
GET /api/stocks/v2/stock-123/quantity

# 재고 증가 (Redis)
PATCH /api/stocks/v2/stock-123/increase
{
  "quantity": 50
}

# 재고 감소 (Redis)
PATCH /api/stocks/v2/stock-123/decrease
{
  "quantity": 30
}
```

## 🔄 HTTP 테스트 파일

`StockApplication.http` 파일에 V1과 V2 API를 모두 테스트할 수 있는 요청들이 포함되어 있습니다:

### V2 API 테스트 섹션
- **V2-1, V2-2**: 재고 등록 (Redis)
- **V2-3, V2-4**: 재고 조회 및 수량 조회 (Redis)
- **V2-5 ~ V2-8**: 재고 증가 테스트 (성공, 에러 케이스)
- **V2-9 ~ V2-13**: 재고 감소 테스트 (성공, 에러 케이스)

### 테스트 시나리오
- ✅ 기본 기능 테스트
- ✅ 에러 케이스 테스트 (존재하지 않는 재고, 수량 부족)
- ✅ 경계값 테스트 (0, 음수)
- ✅ 입력값 검증 테스트
- ✅ 성능 테스트
- ✅ 동시성 테스트

### 사용 방법
1. V2-1, V2-2로 재고 등록
2. 응답에서 `stockId` 복사
3. 다른 테스트 케이스에서 `{stockId}` 부분을 실제 ID로 교체
4. 순차적으로 테스트 실행

## 🎯 장점

### 1. **유연한 선택**
- 상황에 따라 V1 또는 V2 선택 가능
- 점진적 마이그레이션 지원

### 2. **성능 최적화**
- Redis의 인메모리 특성 활용
- 원자적 연산으로 동시성 보장

### 3. **안정성 유지**
- 기존 V1 시스템 그대로 유지
- 장애 시 V1으로 롤백 가능

### 4. **확장성**
- 헥사고날 아키텍처로 새로운 저장소 추가 용이
- 마이크로서비스 환경에서 독립적 운영 가능

### 5. **성능 최적화**
- Redis 인메모리 특성으로 최고 성능
- 원자적 연산으로 동시성 보장
- 불필요한 정보 제외로 응답 크기 최적화

### 6. **운영 안정성**
- V1과 V2 병행 운영으로 장애 대응
- Redis 휘발성에 대한 명확한 인지
- 점진적 마이그레이션 지원

## 🔮 향후 계획

1. **모니터링 강화**
   - V1 vs V2 성능 메트릭 수집
   - Redis 메모리 사용량 모니터링

2. **캐시 전략 개선**
   - Redis Cluster 구성
   - 백업 및 복구 전략 수립

3. **A/B 테스트**
   - 트래픽 분산으로 V1 vs V2 성능 비교
   - 사용자 경험 개선

## 📝 결론

Redis 기반 V2 재고 관리 시스템을 성공적으로 구현하여, 기존 JPA 시스템과 병행 운영할 수 있는 환경을 구축했습니다. 헥사고날 아키텍처를 준수하여 유지보수성과 확장성을 확보했으며, 포괄적인 테스트 커버리지로 안정성을 보장합니다.

---

**Reviewer 참고사항:**
- 모든 테스트 통과 확인
- 기존 V1 API 호환성 유지
- 헥사고날 아키텍처 원칙 준수
- 예외 처리 및 검증 로직 완성도 확인 