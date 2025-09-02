# Dockerfile - 추천 방법
FROM elastic/elasticsearch:9.0.3

# Nori 분석기 플러그인 설치
RUN elasticsearch-plugin install analysis-nori

# 개발 환경을 위한 설정
ENV discovery.type=single-node
ENV xpack.security.enabled=false

# 플러그인 설치 확인
RUN elasticsearch-plugin list
