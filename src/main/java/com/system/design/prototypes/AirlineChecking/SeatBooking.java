package com.system.design.prototypes.AirlineChecking;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class SeatBooking {
    AirlineDAO airline;
    SeatBooking(AirlineDAO airline) {
        this.airline = airline;
    }

    public String bookSeatsApproach1() {
        return "select seat_id from flight where user_id is null limit 1;";
    }

    // This method will lock the rows for the result set and all transactions who want to access the same row will wait and then execute the select again
    public String bookSeatsApproach2() {
        return "select seat_id from flight where user_id is null limit 1 for update;";
    }

    //
    public String bookSeatsApproach3() {
        return "select seat_id from flight where user_id is null LIMIT 1 for update skip locked;";
    }

    public int bookSeats(AirlineDAO.UserDAO user, int approach) {
        var seatId = -1;
        String getSeatIdQuery = switch (approach) {
            case 1 -> bookSeatsApproach1();
            case 2 -> bookSeatsApproach2();
            default -> bookSeatsApproach3();
        };

        try (Connection db = airline.getConnection()) {
            db.setAutoCommit(false);
            // Write a query to get the first available seat
            var preparedStatement = db.prepareStatement(getSeatIdQuery);
            var result = preparedStatement.executeQuery();
            if(result.next()){
                System.out.println("the seat selected"+seatId+" by user "+ user.name);
                seatId = result.getInt("seat_id");

                // Even if in the first statement we have the row, since there was no unique index on the seat_id it has to scan the whole table again to find the seat_id row so it locks the whole table
                var pstmt2 = db.prepareStatement("update flight set user_id = ? where seat_id = ? ");
                pstmt2.setInt(1, user.user_id);
                pstmt2.setInt(2, seatId);
                var result2 = pstmt2.executeUpdate();
                if (result2 > 0) {
                    // Commit the transaction if the update was successful
                    db.commit();
                    System.out.println("Transaction committed successfully. by user "+user.name);
                } else {
                    // Rollback if no rows were updated
                    db.rollback();
                    System.out.println("Transaction rolled back due to no update. by user "+user.name);
                }
            } else {
                db.rollback();
                System.out.println("No seat found user "+ user.name);
            }
            // Update in DB the user_id with the seat id
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Query did not execute successfully");
        }
        return seatId;
    }

    public void simulateUsersBooking(int approach) {
        // total 120 seats
        int row=10;
        int column=12;
        int[][] filled = new int[row][column];
        var users = airline.getUsers();
        var threadsList = new ArrayList<Thread>();
        var startInstant= Instant.now();
        for(var user: users) {
            var t = new Thread(() -> {
                int seatID = bookSeats(user, approach);
                if(seatID==-1) {
                    System.out.println("Could not assign the user " + user.name + " a seat ");
                }
                else {
                    System.out.println(user.name + " was assigned seat "+seatID);
                    int i=(seatID-1)/column;
                    int j=(seatID-1)%column;
                    filled[i][j]=1;
                }
            });
            t.start();
            threadsList.add(t);
        }
        for(var thread: threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e){
                System.out.println("Thread interrupted exception");
            }
        }
        var endInstant = Instant.now();
        var diff = Duration.between(startInstant, endInstant);
        System.out.println("Time taken "+ diff.toMillis() + " ms");
        for (int[] ints : filled) {
            for (int anInt : ints) {
                if (anInt == 1)
                    System.out.print("*");
                else
                    System.out.print(".");
            }
            System.out.println("");
        }
    }

    public void clearAssignment() {
        int noOfUsers = 120;
        String updateAssignmentForEachUserAsNull = "update flight set user_id=null";
        Connection db = null;
        try {
            db= airline.getConnection();
            var prprStmt = db.prepareStatement(updateAssignmentForEachUserAsNull);
            prprStmt.executeUpdate();
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


    public static void main(String[] args) {
        AirlineDAO airlineDAO = new AirlineDAO();
//        Run these lines once before running the test
//        airlineDAO.generateFlight();
//        airlineDAO.generateUsers();

        var booking = new SeatBooking(airlineDAO);
        booking.clearAssignment();
        booking.simulateUsersBooking(3);
    }
}