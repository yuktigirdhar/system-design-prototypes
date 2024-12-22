package com.system.design.prototypes.AirlineChecking;

import com.github.javafaker.Faker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AirlineDAO {
    public class UserDAO {
        public UserDAO(int u, String n){
            user_id=u;
            name=n;
        }
        public int user_id;
        public String name;
    }

    public List<UserDAO> getUsers() {
        var users= new ArrayList<UserDAO>();
        String getUsersQuery = "select user_id, name from user";
        Connection db = null;
        try {
            db= getConnection();
            var prprStmt = db.prepareStatement(getUsersQuery);
            var result = prprStmt.executeQuery();
            while(result.next()){
                int userID=result.getInt("user_id");
                String name=result.getString("name");
                users.add(new UserDAO(userID, name));
            }

        } catch(SQLException | ClassNotFoundException e){
            System.out.println("SQL Exception "+e);
        } finally {
            if (db != null) {
                try {
                    db.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    // Handle exception while closing
                    e.printStackTrace();
                }
            }
        }
        return users;
    }


    public void generateUsers() {
        int noOfUsers = 120;
        String insertUsers = "insert into user (name) values (?)";
        Connection db = null;
        Faker faker = new Faker();
        try {
            db= getConnection();
            for(int i=0;i<noOfUsers;i++) {
                var prprStmt = db.prepareStatement(insertUsers);
                prprStmt.setString(1, faker.name().name());
                prprStmt.executeUpdate();
            }
        } catch(SQLException | ClassNotFoundException e){
            System.out.println("SQL Exception "+e);
        } finally {
            if (db != null) {
                try {
                    db.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    // Handle exception while closing
                    e.printStackTrace();
                }
            }
        }
    }

    public void generateFlight() {
        int noOfseats = 120;
        String insertUsers = "insert into flight (seat_id) values (?)";
        Connection db = null;
        try {
            db= getConnection();
            for(int i=0;i<noOfseats;i++) {
                var prprStmt = db.prepareStatement(insertUsers);
                prprStmt.setInt(1,i+1);
                prprStmt.executeUpdate();
            }
        } catch(SQLException | ClassNotFoundException e){
            System.out.println("SQL Exception "+e);
        } finally {
            if (db != null) {
                try {
                    db.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    // Handle exception while closing
                    e.printStackTrace();
                }
            }
        }
    }



    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver"); // Is this class loader??
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/airline",
                "root", "");
    }

}
