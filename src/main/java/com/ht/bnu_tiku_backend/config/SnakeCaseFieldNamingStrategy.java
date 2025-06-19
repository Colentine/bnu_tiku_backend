package com.ht.bnu_tiku_backend.config;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.util.StringUtils;

public class SnakeCaseFieldNamingStrategy implements FieldNamingStrategy {

    @Override
    public String getFieldName(PersistentProperty<?> property) {
        return toSnakeCase(property.getName());
    }

    private String toSnakeCase(String input) {

        if (!StringUtils.hasText(input)) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }
}
