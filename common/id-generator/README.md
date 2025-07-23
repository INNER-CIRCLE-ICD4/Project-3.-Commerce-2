### 사용법
```kotlin
dependencies {
    implementation(project(":common:id-generator"))
    // 기타 의존성들...
}
```


```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedULID  // 추가
    private String id;

    private String name;
    private String description;

    // 생성자, 메서드들...
}

```

## 🚨 주의사항
1. **Hibernate 버전**: 6.0 이상에서만 `@IdGeneratorType` 사용 가능
2. **ID 타입**: 반드시 타입 사용 `String`
3. **수동 할당 불가**: `@GeneratedULID` 사용 시 ID를 수동으로 설정하면 안 됨
4. **트랜잭션**: JPA의 `persist()` 시점에 ID가 생성됨
