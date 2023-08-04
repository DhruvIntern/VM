package com.vaidhyamegha.tools.data_generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    private final String name;
    private Map<String, String> attributes;
    private boolean isEnum;
    private List<String> enumValues;
    private int count;

    public Entity(String name) {
        this.name = name;
    }

    public Entity(String name, Map<String, String> attributes, int count) {
        this.name = name;
        this.attributes = attributes;
        this.count = count;
    }

    public Entity(String name, Map<String, String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public Entity(String name, Map<String, String> attributes, List<String> enumValues) {
        this.name = name;
        this.attributes = attributes;
        this.isEnum = false;
        this.enumValues = enumValues;
    }

    public String getName() {
        return name;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public int getCount() {
        return count;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("entity", name);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            row.put(entry.getKey(), entry.getValue());
        }
        data.add(row);
        return data;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
