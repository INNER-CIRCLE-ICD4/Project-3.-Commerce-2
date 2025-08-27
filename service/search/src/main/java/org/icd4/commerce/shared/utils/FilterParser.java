package org.icd4.commerce.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class FilterParser {

    /**
     * filter=color:red,black|size:S,M,L
     * -> ["color:red", "color:black", "size:S", "size:M", "size:L"]
     */
    public List<String> parseToFlattenedFilters(String filterString) {
        if (filterString == null || filterString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> flattenedFilters = new ArrayList<>();

        try {
            String[] filterGroups = filterString.split("\\|");

            for (String group : filterGroups) {
                addFlattenedGroup(group.trim(), flattenedFilters);
            }

        } catch (Exception e) {
            log.warn("필터 파싱 중 오류 발생: {}", filterString, e);
        }

        return flattenedFilters;
    }

    private void addFlattenedGroup(String group, List<String> flattenedFilters) {
        if (group.isEmpty()) return;

        String[] parts = group.split(":", 2);
        if (parts.length != 2) {
            log.warn("잘못된 필터 형식: {}", group);
            return;
        }

        String key = parts[0].trim();
        String values = parts[1].trim();

        // "color:red,black" -> ["color:red", "color:black"]
        Arrays.stream(values.split(","))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .map(v -> key + ":" + v)  // "color:red" 형태로 변환
                .forEach(flattenedFilters::add);
    }
}