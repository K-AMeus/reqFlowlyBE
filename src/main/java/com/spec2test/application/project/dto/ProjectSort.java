package com.spec2test.application.project.dto;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum ProjectSort {
    NAME("name"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");


    private final String property;

    ProjectSort(String property) {
        this.property = property;
    }

    public Sort toSort(Sort.Direction dir) {
        return Sort.by(dir, property);
    }

    public static ProjectSort from(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value)
                        || e.property.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown orderBy value: " + value));
    }
}
