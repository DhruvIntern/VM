package com.vaidhyamegha.tools.data_generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scalar {
    Map<String, List<String>> map;
    private final String entity;
    private final String attribute;
    private final String pattern;

    public Scalar(String entity, String attribute, String pattern, Map<String, List<String>> map) {
        this.entity = entity;
        this.attribute = attribute;
        this.pattern = pattern;
        this.map = new HashMap<>();
    }

    public String getEntity() {
        return entity;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getPattern() {
        return pattern;
    }
}

