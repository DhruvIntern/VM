package com.vaidhyamegha.tools.data_generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.github.curiousoddman.rgxgen.RgxGen;

public class DataGenerator {
    private static final String DELIMITER = "|";
    private static final String NEW_LINE_SEPARATOR = "\n";
    static Map<String, List<String>> enumTypes = new HashMap<>();
    static List<List<String>> temp = new ArrayList<>();
    static Map<String, List<List<String>>> map = new HashMap<>();//this is to store values of entites whose values are generated.
    static String ans = "";
    static int numRows = 0;
    static GraphQLModel model;

    public static void main(String[] args) {
        String inputFilePath = null;
        // Parse the command line arguments and get the inputfile Path.
        for (int i = 0; i < args.length; i++) {
            if ("-f".equals(args[i])) {
                inputFilePath = args[i + 1];
            }
        }

         //If we dont find the inputFilePath ,specify to use the appropriate command.
        if (inputFilePath == null) {
            System.err.println("Usage: java -jar data-generator.jar -i <input-file>");
            System.exit(1);
        }

         /*
         * model is the object of of GraphQLModel.java class and we will
         * be using this to access the various methods defined in the
         * mentioned class
         */

        model = new GraphQLModel(new File(inputFilePath));

         //Now from this model, extract all the entities and enums present in it.
        List<Entity> entities = model.getEntities();
        enumTypes = model.getEnumTypes();

        //For every entity we get from the model, call the writeEntityToCsv function
        for (Entity entity : entities) {
            writeEntityToCsv(entity);
        }
    }

      /**
     * this method is invoked from {@link #main(String[])} line 64
     * this method will be responsible for writing the values generated
     * by the DataGenerator.java class to the CSV's for every entity in
     * the graphQL model
     * 
     * @param entity 
     */

    private static void writeEntityToCsv(Entity entity) {

         //Create a new file with same name as Entity with extension of '.csv'
        String fileName = entity.getName() + ".csv";
        File file = new File(fileName);
        
        try (FileWriter writer = new FileWriter(file)) {
            // Write the header row
            writer.append(String.join(DELIMITER, entity.getAttributes().keySet()));
            writer.append(NEW_LINE_SEPARATOR);

            // Write the data rows
            int numRows = entity.getCount() != 0 ? entity.getCount() : 10; // set the number of rows to generate

             //Now we have to write code to generate data for 'numRows' Rows for this particular Entity.
            for (int i = 0; i < numRows; i++) {

                List<String> values = new ArrayList<>();//This will contain values of each seperate rows generated.
                String type = ""; //here we will store attribute type..

               //This for loop will calculate value for each Attribute and at last append that value in List<String> values.

                for (String attribute : entity.getAttributes().keySet()) {

                   // System.out.println(entity.getName()+"."+attribute);
                    type = entity.getAttributes().get(attribute);
                    String value = "";//here we will store attribute value..

                // First check whether that entity is an ENUM , if it is get all the ENUMS in a List and 
                // select a random ENUM and store in 'value' String.

                    if (entity.isEnum() && type.startsWith("enum")) {
                        List<String> enumValues = entity.getEnumValues();
                        value = enumValues.get(new Random().nextInt(enumValues.size()));
                    }
                    
                    else if (model.getEntitiesName().contains(type.substring(0, type.length() - 1))) {

                         //If map already contains the value for that referenced Entity so return a random value..
                        if (map.containsKey(type.substring(0, type.length() - 1))) {
                            List<List<String>> g = map.get(type.substring(0, type.length() - 1));

                            Random random = new Random();
                            int max  =g.size()-1;
                            int ind = random.nextInt(max - 0 + 1) + 0;
                             //to get ID we have used 0th index
                            values.add(g.get(ind).get(0).toString());
                            //values.add(g.toString());
                        }
                         else {
                            Entity referencedEntity = null;
                            //Find for which entity is used as value for this particluar Attribute..
                            for (Entity e : model.getEntities()) {
                                if (e.getName().equals(type.substring(0, type.length() - 1))) {
                                    referencedEntity = e;
                                    break;
                                }
                            }
                            //Here if reference entity is not null, pass the respective data you want .
                            if (referencedEntity != null) {
                                //Now we have to store the values for ReferencedEntity so first call function for it.
                                writeEntityToCsv(referencedEntity);

                                 List<List<String>> g = map.get(type.substring(0, type.length() - 1));
                                if (g != null){
                                    Random random = new Random();
                                    int max  =g.size()-1;
                                   int ind = random.nextInt(max - 0 + 1) + 0;
                                   //to get ID we have used 0th index
                                   values.add(g.get(ind).get(0).toString());
                                }
                               
                            }
                             else {
                                System.out.println("Referenced Entity is null");
                            }
                        }
                    }
                    
                    else {
                        //This condition checks if the model contains a scalar map entry for the given type
                        if(model.getScalarMap().containsKey(type.substring(0, type.length()-1))){
                            String pattern = model.getScalarMap().get(type.substring(0, type.length()-1));
                            value =generateRandomRegex(pattern);
                            values.add(value);
                            continue;
                        }

                         //This condition checks if the model contains a date constraint map entry for the given entity and attribute
                        if(model.getConstraintDateMap().containsKey(entity.getName()+"."+attribute)){
                           // System.out.println(1);
                            List<LocalDate>al  = model.getConstraintDateMap().get(entity.getName()+"."+attribute);
                            Random random = new Random();
                            LocalDate randomDate = al.get(0).plusDays(random.nextInt((int) ChronoUnit.DAYS.between(al.get(0), al.get(1))));
                            values.add(randomDate.toString());
                            continue;
                        }

                        //This condition checks if the model contains a general constraint map entry for the given entity and attribute
                        if(model.getConstraintsMap().containsKey(entity.getName()+"."+attribute)){
                           List<Long> al = model.getConstraintsMap().get(entity.getName()+"."+attribute);
                           long min = al.get(0);
                           long max=al.get(1);
                           value= generateRandomValue(type,min, max);
                           values.add(value);
                           continue;
                        }
                         //For any other 'type' of Attribute we will call below function.
                        value = generateRandomValue(type);
                    }
                    //here loop ends after getting values for every attribute of that entity... 
                //and so append these values
                    values.add(value);
                    //map.put(entity.toString(),values);
                   
                  
                          if(map.containsKey(entity.toString())){
                              map.get(entity.toString()).add(values);
                          }

                        map.computeIfAbsent(entity.toString(), k-> new ArrayList<>()).add(values);
                       
                
                    }
                   
                writer.append(String.join(DELIMITER, values));
                writer.append(NEW_LINE_SEPARATOR);
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV file for entity " + entity.getName());
            e.printStackTrace();
        }
    }

       /**
     * This method generates value based on the type of the attribute in graphql model
     * 
     * @param type
     * @return String 
     */

    private static String generateRandomValue(String type) {
         /*
         * the below for loop is used only if the enumTypes isnt empty
         * else its skipped and the flow moves to line 226
         */

         //

         for(var e : model.getEnumDistribution().entrySet()){
            String key = e.getKey(); // Status.Fail
            String val = e.getValue(); // decimal val
            String[] split = key.split("\\.");
            String firstPart = split[0].trim();
            String secondPart = split[1].trim();

            if (type.substring(0, type.length() - 1).equals(firstPart)) {


                long dist = (long)(numRows* Double.parseDouble(val));
                // System.out.println(dist);
                for(long d = 1; d<dist; d++){
                   //something needs to be done here 
                }
            }
            // break;
            continue;
        }


        //This loop will generate a random ENUM value for that particular ENUM and return it.
        for (Map.Entry<String, List<String>> e : enumTypes.entrySet()) {
            String key = e.getKey();
            List<String> val = e.getValue();
            if (type.substring(0, type.length() - 1).equals(key)) {
                ans = enumTypes.get(key).get(new Random().nextInt(val.size())); 
                return ans;
            }
        }

       //For rest of the types..
        switch (type) {
            case "ID!":
                ans = Integer.toString((int) (Math.random() * 1000000));
                break;
            case "Int!":
                Random randomNum = new Random();
                int random = randomNum.nextInt(100);
                ans = Integer.toString(random);
                break;
            case "String!":
                ans = generateRandomString();
                break;
            case "Date!":
                ans = generateRandomDate();
                break;
            case "Float!":
                ans = Float.toString((float) (Math.random() * 100));
                break;
            case "Time!":
                ans = generateRandomTime();
                break;
            default:
                ans = "--";
                break;
        }

        return ans;
    }

     /**
     * This is an overloaded method and can be used in case we 
     * want to pass 2 more parameters as input to allow printing
     * between the min and max range
     * 
     * @param type -> type of the attribute
     * @param min -> takes the min integer as an argument
     * @param max -> takes the max integer as an argument
     * @return ans in the form of String
     */
    private static String generateRandomValue(String type, long min, long max) {
        switch (type) {
            case "ID!":
                ans = Integer.toString((int) (Math.random() * 1000000));
                break;
            case "Int!":
                Random randomNum = new Random();
                int random = randomNum.nextInt(100);
                ans = Integer.toString(random);
                break;
            case "String!":
                ans = generateRandomString(min, max);
                break;
            case "Date!":
                ans = generateRandomDate();
                break;
            case "Float!":
                ans = Float.toString((float) (Math.random() * 100));
                break;
            case "Time!":
                ans = generateRandomTime();
                break;
            default:
                ans = "--";
                break;
        }
        return ans;
    }

    public static String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = (int) (Math.random() * 10) + 5;
        StringBuilder builder = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (new Random().nextFloat() * (rightLimit - leftLimit + 1));
            builder.append((char) randomLimitedInt);
        }
        return builder.toString();
    }

    /**
     * it returns a random date between minDay and maxDay
     * 
     * @return date in the form of a String
     */

    public static String generateRandomDate() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2023, 4, 22).toEpochDay();
        long randomDay = minDay + (int) (Math.random() * (maxDay - minDay));
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return randomDate.toString();
    }

    public static String generateRandomDate(String minDate) {
        try {
            // Parse the minimum date parameter
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date minDateObj = dateFormat.parse(minDate);

            // Get the current date and time
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            // Set the maximum date to the current date
            long maxDateMillis = calendar.getTimeInMillis();

            // Calculate the range in milliseconds
            long rangeMillis = maxDateMillis - minDateObj.getTime();

            // Generate a random date within the specified range
            Random random = new Random();
            long randomDateMillis = minDateObj.getTime() + (long) (random.nextDouble() * rangeMillis);

            // Create a new Date object from the random millis
            Date randomDate = new Date(randomDateMillis);

            // Format the random date as "DD/MM/YYYY"
            return dateFormat.format(randomDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid minimum date format");
        }
    }

    /**
     * this is an overloaded method and generates a string 
     * within the specified constraint of min and max
     * 
     * @param min -> lower bound of the string length , should be > 0 && < max
     * @param max -> upperbound of the string length , should be > min
     * @return a random String
     */

    private static String generateRandomString(long min, long max) {
        Random random = new Random();
        long length = min + random.nextLong(max - min + 1L);
        StringBuilder sb = new StringBuilder();
        for (long i = 0; i < length; i++) {
            char c = (char) (random.nextLong(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 
     * @return a random time of the format hh:mm 
     */

    public static String generateRandomTime() {
        Random rand = new Random();
        int hour = rand.nextInt(24);
        int minute = rand.nextInt(60);
        LocalTime time = LocalTime.of(hour, minute);
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        ZonedDateTime istTime = ZonedDateTime.of(LocalDate.now(), time, istZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a z");
        return istTime.format(formatter);
    }

     /**
     * this method generates a string based on the 
     * regex pattern defined by the scalars in any
     * graphql model
     * 
     * RgxGen library has been used to generate a string based
     * on the regex pattern
     * 
     * @param pattern
     * @return String based on the pattern
     */

    public static String generateRandomRegex(String pattern) {
        RgxGen rgxGen = new RgxGen(pattern);
        String s = rgxGen.generate();
        return s;
    }
}
