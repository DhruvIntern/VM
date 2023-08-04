package com.vaidhyamegha.tools.data_generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphQLModel {
    Pattern ENTITY_PATTERN = Pattern.compile("type\\s+(\\w+)\\s*(?:@count\\((\\d+)\\))?\\s*\\{([^}]+)\\}", Pattern.DOTALL);
    Pattern ENUM_PATTERN = Pattern.compile("enum\\s+(\\w+)\\s*\\{([^}]+)(?:\\s*@constraint\\s*\\(([^)]+)\\))?\\s*}");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("(\\w+)\\s*:\\s*((\\w+!?)|(\\[[^\\]]*\\])+)(\\s*@constraint\\(([^)]*)\\))?");
    Pattern SCALAR_PATTERN = Pattern.compile("scalar\\s+(\\w+)\\s+\"([^\"]*)\"");

    private List<Entity> entities = new ArrayList<>();
    private List<EnumType> enumTypes = new ArrayList<>();
    Map<String, List<String>> map = new HashMap<>();
    Map<String,String>scalarDetails = new HashMap<>();
    Map<String,List<Long>>constraintsMap = new HashMap<>();
    Map<String,List<LocalDate>> constraintDateMap = new HashMap<>();
    Map<String,String> enumDistribution = new HashMap<>();

    /**
     * this constructor is used by the 
     * Datagenerator class {@link DataGenerator#main} line 52
     * @param inputFile
     */
    public GraphQLModel(File inputFile) {
        try {
            //Reading the input file and storing the Enities present in entityMatcher.
            String input = new String(Files.readAllBytes(inputFile.toPath()));
            Matcher entityMatcher = ENTITY_PATTERN.matcher(input);

            if (!entityMatcher.find()) {
                System.out.println("Unable to find entities in input.");
            }

            do {
                //Extract the Entity name,count,Attributes present
                String name = entityMatcher.group(1);
                String countString = entityMatcher.group(2);
                String attributesString = entityMatcher.group(3);

                Map<String, String> attributes = new LinkedHashMap<>();//This will store Attribute Name and its Type.
                Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(attributesString);

                //Extracting all the Attributes present in that Entity.
                while (attributeMatcher.find()) {
                    String attributeName = attributeMatcher.group(1);
                    String attributeType = attributeMatcher.group(2);

                    //Also check for constraint if present any.
                    if (attributesString.contains("constraint")) {
                        String constraintContent = attributeMatcher.group(6);
                         if(hasStringSymbol(constraintContent)){
                            System.out.println(attributeName);
                            System.out.println(constraintContent+" has been skipped");
                            continue;
                        }


                        if (constraintContent != null) {
                            String[] constraints = constraintContent.split(",");

                            if(attributeType.equals("Date!")){
                                String min = constraints[0].replaceAll("[^\\d/]", "");
                                String max = constraints.length > 1 ? constraints[1].replaceAll("[^\\d/]", "") : null;
                                List<LocalDate> al = new ArrayList<>();
                                if (min != null) {
                                    LocalDate minDate = LocalDate.parse(min, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                    al.add(minDate);
                                } else {
                                    al.add(LocalDate.now());
                                }   
                                if (max != null) {
                                    LocalDate maxDate = LocalDate.parse(max, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                    al.add(maxDate);
                                } else {
                                    al.add(LocalDate.now());
                                }
                                constraintDateMap.put(name + "." + attributeName, al);
                            }
                            else {
                                String min = constraints[0].replaceAll("\\D+", "");
                                String max = constraints.length > 1 ? constraints[1].replaceAll("\\D+", "") : null;
                                List<Long> al = new ArrayList<>();
                                if (min != null) al.add(Long.parseLong(min));
                                else al.add(0L);
                                if (max != null) al.add(Long.parseLong(max));
                                else al.add(0L);
                                constraintsMap.put(name + "." + attributeName, al);
                            }
                        }
                    }
                    attributes.put(attributeName, attributeType);
                }

                //After getting all the information about an Entity , clal function 'Entity' with appropriate info as parameters.
                Entity entity;
                if (countString != null) {
                    int count = Integer.parseInt(countString);
                    entity = new Entity(name, attributes, count);
                } else {
                    entity = new Entity(name, attributes);
                }
                entities.add(entity);
            } while (entityMatcher.find());

            //an enum (short for enumeration) is a special data type that represents a fixed set of constants. It is used to define a collection of related values that are mutually exclusive.
            //It is used when we have soomething that has pre-defined values , that do not change..
            if (input.contains("enum")) {
                Matcher enumMatcher = ENUM_PATTERN.matcher(input);
                if (!enumMatcher.find()) {
                    System.out.println("Unable to find enums in input.");
                }

                do {
                    String name = enumMatcher.group(1);
                    String valuesString = enumMatcher.group(2);

                     if(valuesString.contains("constraint")){
                        Pattern pattern = Pattern.compile("(\\w+)\\s*@constraint\\s*\\(\\s*distribute\\s+([0-9]+(?:\\.[0-9]+)?)\\s*\\)");
                        Matcher matcher = pattern.matcher(valuesString);
                       while (matcher.find()) {
                            String passVariable = matcher.group(1);
                            String decimalValue = matcher.group(2);
                            enumDistribution.put(name+"."+passVariable,decimalValue);
                        }
                    }

                    List<String> values = new ArrayList<>();
                    String[] valuesArray = valuesString.trim().split("\\s+");
                    for (String value : valuesArray) {
                        if (!value.isEmpty()) {
                            if(value.contains("(") || value.contains(")"))continue;
                            if(value.contains("@"))continue;
                            values.add(value);
                        }
                    }

                    map.put(name, values);
                    EnumType enumType = new EnumType(name, values);
                    enumTypes.add(enumType);
                } while (enumMatcher.find());
            }

            //scalars are primitive data types...like int,long ,float,double,boolean etc....
            if (input.contains("scalar")) {
                Matcher scalarMatcher = SCALAR_PATTERN.matcher(input);
                if (!scalarMatcher.find()) {
                    System.out.println("Unable to findscalars in input.");
                }
                do {
                    String scalarName = scalarMatcher.group(1);
                    String scalarAttribute = scalarMatcher.group(2);
                    scalarDetails.put(scalarName, scalarAttribute);
                } while (scalarMatcher.find());
                System.out.println(scalarDetails);
            }

} 
        
catch (IOException e) {
            System.err.println("Error reading input file: " + inputFile.getPath());
            e.printStackTrace();
        }
}


    /**
     * used by the Datagenerator class to get list of entities 
     * present in the graphQL model
     * 
     * @return list of object entites 
     */
    public List<Entity> getEntities() {
        return entities;
    }
    
    /**
     * used by Datagenerator class to if the entities name is
     * same as that of an attributes type 
     * 
     * {@code 
        * type User {
                post: Post!
                dob: Date! @constraint(min: 23/08/2022, max: 30/08/2022)
            }

            type Post {
                author: User!
            }
     * }
     * In the above code attribute post has the same type as that
     * of an entity Post 
     * 
     * @return list of entities name 
     */
    public List<String> getEntitiesName() {
        List<String> newList = new ArrayList<>();
        for (Entity e : entities) {
            newList.add(e.toString());
        }
        return newList;
    }

    /**
     * @return a map with all the entries in an enum
     * 
     * key -> String that shows the name of an Enum
     * value -> list of all the values that can be accepted by the enum
     */
    public Map<String, List<String>> getEnumTypes() {
        return map;
    }

    /**
     * 
     * @return a map with scalar details 
     * 
     * key -> string that shows the name of the scalar such as Name or Email
     * value -> regex that is linked with that particular scalar
     */
    public Map<String,String> getScalarMap(){
        return scalarDetails;
    }

    /**
     * @return map with all the constraints present in graphQL 
     * 
     * key -> string that shows which attribute has a constraint linked to it
     * value -> list of min and max integers defined in the constraint
     */
    public Map<String, List<Long>> getConstraintsMap() {
        return constraintsMap;
    }

    /**
     * @return map with date constraints
     * key -> string that shows which attribute has a constraint linked to it
     * value -> list of Locale Date between the specified constraints
     */
    public Map<String,List<LocalDate>> getConstraintDateMap(){
        return constraintDateMap;
    }

      public static boolean hasStringSymbol(String s){
        if(s==null)return false;
        for(int i=0 ;i<s.length();i++){
            if(s.charAt(i)==60 ||s.charAt(i)==62)return true;
        }
        return false;
    }

   
    public Map<String,String> getEnumDistribution() {
        return enumDistribution;
    }
  

}
