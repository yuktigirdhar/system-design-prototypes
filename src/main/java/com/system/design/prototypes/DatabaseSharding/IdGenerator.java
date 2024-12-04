package com.system.design.prototypes.DatabaseSharding;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    // Increasing integer id
    /*
    *  timestamp in utc which is a  long epoch time + machine id (api server generating it) +
    *  single thread calling id generation within same time
    *  multiple threads calling id generation within same time
    *  Have a counter : Within the server a global entity accessible to all threads
    *  What if server goes down?
    *  You save the value of the counter to disk: Save it everytime or save it every n seconds or nth time it is called
    *  What should be the value of this n?
    *  if the sever went down on n/2th count before saving to disk
    *  on coming back up? : you need to read from file
    * */
    AtomicInteger counter; // global counter used by all threads for this server
    int n; // we are not touching this value
    int machineId;
    String sdDirectory;

    IdGenerator() {
        // create directory for id-generator inside if not already present
        // create file name counter.txt
        // TODO: try loading these hard coded value from application properties
        sdDirectory = "";
        n=10; // N is how often do you want to save the counter to file
        System.out.println(machineId);
    }

    // Will there be a separate file for each table for which a unique id needs to be created- Yes?
    synchronized public int generateId(String tableName) {
        String pathForTable = sdDirectory+tableName+".txt";
        try {
            if (counter == null) { // Either machine restarted or first time
                File file = new File(pathForTable);
                if(!file.exists()) {
                    file.getParentFile().mkdirs();
                    // Create a new file
                    file.createNewFile(); // does this create a text file?
                }
                try (BufferedReader bw = new BufferedReader(new FileReader(pathForTable))) {
                    String value = bw.readLine();
                    value = value != null && !value.isEmpty() ? value : "0";
                    int finalVal = Integer.parseInt(value) + n + 100 ;
                    counter = new AtomicInteger(finalVal);
                }
            } else {
                counter.incrementAndGet();
            }
            if(counter.get() % n == 0) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathForTable))) { // check if this overwrites,
                    // I want to overwrite and fully remove the previous value
                    bw.write(String.valueOf(counter.get()));
                    bw.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Exception occurred "+ e +" "+ e.getMessage());
        }
        return counter.get();
    }
}
