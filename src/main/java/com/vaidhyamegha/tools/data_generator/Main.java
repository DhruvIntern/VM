package com.vaidhyamegha.tools.data_generator;

public class Main {
    public static void main(String[] args) {
        // Define the command line arguments
        String[] cmdArgs = {"-f", "src/main/resources/student.graphql"};

        // Generate entities and write them to CSV files
        DataGenerator.main(cmdArgs);
    }
}

