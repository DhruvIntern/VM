package com.vaidhyamegha.tools.data_generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class GraphQLModelTest {
    @Test
    public void testGraphQLModel() {
        String modelPath = "src/main/resources/datamodel.graphql";
        File modelFile = new File(modelPath);
        GraphQLModel graphQLModel = new GraphQLModel(modelFile);

        Assertions.assertNotNull(graphQLModel.getEntities());
        Assertions.assertNotNull(graphQLModel.getEnumTypes());
        Assertions.assertFalse(graphQLModel.getEntities().isEmpty());
        Assertions.assertFalse(graphQLModel.getEnumTypes().isEmpty());
    }
}


