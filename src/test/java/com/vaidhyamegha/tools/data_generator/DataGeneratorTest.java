package com.vaidhyamegha.tools.data_generator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataGeneratorTest {

    @Test
    void testGenerateRandomString() {
        String s1 = DataGenerator.generateRandomString();
        String s2 = DataGenerator.generateRandomString();
        assertNotEquals(s1, s2);

        assertTrue(s1.matches("[a-z0-9]+"));
    }

    @Test
    void testGenerateRandomDate() {
        String date = DataGenerator.generateRandomDate();
        assertNotNull(date);

        List<String> parts = Arrays.asList(date.split("-"));
        assertEquals(3, parts.size()); // Check that the date has three parts (year, month, day)

        int year = Integer.parseInt(parts.get(0));
        assertTrue(year >= 1970 && year <= 2023); // Check that the year is between 1970 and 2023

        int month = Integer.parseInt(parts.get(1));
        assertTrue(month >= 1 && month <= 12); // Check that the month is between 1 and 12

        int day = Integer.parseInt(parts.get(2));
        assertTrue(day >= 1 && day <= 31); // Check that the day is between 1 and 31
    }

}
