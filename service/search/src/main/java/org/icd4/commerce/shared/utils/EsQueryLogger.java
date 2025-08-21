package org.icd4.commerce.shared.utils;

import co.elastic.clients.json.JsonpSerializable;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.json.stream.JsonGenerator;
import java.io.StringWriter;

@Slf4j
@Component
public class EsQueryLogger {
    private final JacksonJsonpMapper mapper = new JacksonJsonpMapper();

    public String toJson(JsonpSerializable request) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator generator = mapper.jsonProvider().createGenerator(writer);
            request.serialize(generator, mapper);
            generator.close();
            return writer.toString();
        } catch (Exception e) {
            log.warn("Elasticsearch 쿼리 JSON 변환 중 오류 발생", e);
            return "JSON 변환 실패: " + e.getMessage();
        }
    }

    public String toPrettyJson(JsonpSerializable request) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator generator = mapper.jsonProvider()
                    .createGeneratorFactory(null)
                    .createGenerator(writer);
            request.serialize(generator, mapper);
            generator.close();

            return formatJson(writer.toString());
        } catch (Exception e) {
            log.warn("Elasticsearch 쿼리 Pretty JSON 변환 중 오류 발생", e);
            return toJson(request);
        }
    }

    private String formatJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (Exception e) {
            return json;
        }
    }
}