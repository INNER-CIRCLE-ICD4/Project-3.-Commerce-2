package org.icd4.commerce.shared.domain.mapper;

import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AutoCompleteSuggestionGenerator {
    public Completion generate(ProductCreateRequest request) {
        List<String> suggestions = new ArrayList<>();

        if (request.autoCompleteSuggestions() != null) {
            suggestions.addAll(request.autoCompleteSuggestions());
        }

        suggestions.addAll(generateFromProductName(request));
        suggestions.addAll(generateFromBrand(request));

        String[] suggestionArray = suggestions.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .toArray(String[]::new);

        return new Completion(suggestionArray);
    }

    private Set<String> generateFromProductName(ProductCreateRequest request) {
        Set<String> suggestions = new TreeSet<>();
        if (request.name() != null) {
            suggestions.add(request.name());
            suggestions.addAll(Arrays.asList(request.name().split("\\s+")));
        }

        return suggestions;
    }

    private Set<String> generateFromBrand(ProductCreateRequest request) {
        Set<String> suggestions = new TreeSet<>();
        if (request.brand() != null) {
            suggestions.add(request.brand());
            if (request.name() != null) {
                suggestions.add(request.brand() + " " + request.name());
            }
        }

        return suggestions;
    }
}