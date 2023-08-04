package com.vaidhyamegha.tools.data_generator;

import java.util.List;

public class EnumType {

    private final String name;
    private final List<String> values;

    public EnumType(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

}
