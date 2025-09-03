# 개요
이너서클 4기 커머스 2팀 프로젝트 개요


# 도메인 모델
- [상품](service/product/도메인모델.md)
- [구매]()
  - [장바구니]()
  - [주문]()
- [검색](service/search/도메인모델.md)
- [재고](service/product/도메인모델.md)

# 프로젝트 시연
```shell
cd service/search
docker-compose up -d
docker exec es01 ./bin/elasticsearch-plugin install analysis-nori
docker restart es01
```

```shell
#!/bin/bash
curl -X PUT "http://localhost:9200/product_index" \
     -H "Content-Type: application/json" \
     -d @service/search/src/main/resources/elasticsearch/product_index.json

```

```
각 모듈을 dev profile로 실행
```

[시연.http](시연.http)

실행하면서 확인