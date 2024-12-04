package com.system.design.prototypes.DatabaseSharding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DataBaseConnection {
    /**
     *  Create two database connection and add entities user_id and country
     *  Based on country name route to each database
     */

    /* Caching this but if the db is moved because of hot shard problem will have to change this in code,
        and restart the server which is not good practise so in prod, a new db will be there which will hold the details
        of which physical db is the logical database shard in
      * */
    private static final Map<String, String> shardToDB = Map.of();

    public Connection getDbConnection(int connectionId) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); // Is this class loader??

        switch(connectionId){
            case 0:
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/blogs",
                    "root", "");

            case 1:
            default:
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/blogs1",
                    "root", "");
        }
    }

}
