package com.vaidhyamegha.tools.data_generator;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    public void testEntity() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", "1");
        attributes.put("name", "John");
        Entity entity = new Entity("User", attributes);

        assertEquals("User", entity.getName());
        assertFalse(entity.isEnum());
        assertEquals(0, entity.getCount());
        assertEquals(attributes, entity.getAttributes());

        List<Map<String, Object>> data = entity.getData();
        assertEquals(1, data.size());
        Map<String, Object> row = data.get(0);
        assertEquals("User", row.get("entity"));
        assertEquals("1", row.get("id"));
        assertEquals("John", row.get("name"));
    }

    @Test
    public void testEnumEntity() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", "1");
        attributes.put("name", "Color");
        Entity entity = new Entity("Color", attributes, List.of("Red", "Green", "Blue"));

        assertEquals("Color", entity.getName());
        assertFalse(entity.isEnum());
        assertEquals(0, entity.getCount());
        assertEquals(attributes, entity.getAttributes());
        assertEquals(List.of("Red", "Green", "Blue"), entity.getEnumValues());

        List<Map<String, Object>> data = entity.getData();
        assertEquals(1, data.size());
        assertEquals("Color", data.get(0).get("entity"));

        // Test non-enum Entity
        Entity nonEnumEntity = new Entity("Person", attributes);
        assertFalse(nonEnumEntity.isEnum());
    }


    @Test
    public void testCountedEntity() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", "1");
        attributes.put("name", "John");
        Entity entity = new Entity("User", attributes, 5);

        assertEquals("User", entity.getName());
        assertFalse(entity.isEnum());
        assertEquals(5, entity.getCount());
        assertEquals(attributes, entity.getAttributes());

    }
}

