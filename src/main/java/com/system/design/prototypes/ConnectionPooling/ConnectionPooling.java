package com.system.design.prototypes.ConnectionPooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class ConnectionPooling {
    int n = 10;
    int noOfThreads=4000;
    Connection[] queue= new Connection[n]; // Max connections is 10 for this API Server
    int pointer=0; // mutex and queue pointer
    private void fillConnection() throws SQLException {
        for(int i=0; i< queue.length; i++) {
            queue[i] = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        }
    }

    private void blockingQueueConnections() throws SQLException {
        fillConnection();
        // simulate the requests these 200 requests are multi-threaded
        var threads = new ArrayList<Thread>();
        var startTime = Instant.now();
        for(int i=0;i<noOfThreads;i++) {
            // create a thread and request for a db connection
            var t = new Thread( () -> {
                long threadId = Thread.currentThread().getId();

                try {
                    waitAndRemove();
                    Connection conn = queue[pointer];
                    Statement stmt = conn.createStatement();
                    // Execute a sleep query
                    stmt.execute(""); // Sleep for 1 secon
                    Thread.sleep(2000);
                    waitAndAdd();
                } catch(Exception e) {
                    System.out.println(e + " "+e.getMessage());
                    System.out.println("Thread unable to get connection");
                }
            });
            threads.add(t);
        }

        for(var thread: threads) {
            try {
                thread.join();
            } catch(InterruptedException e){
                System.out.println("Thread join exception");
            }
        }
        var endTime = Instant.now();
        var duration = Duration.between(startTime, endTime);
        System.out.println("Total miliseconds "+duration.toMillis());
    }

    private void nonBlockingQueueMaxConnections() {
        var threads = new ArrayList<Thread>();
        Instant startTime = Instant.now();

        for(int i=0;i<noOfThreads;i++) {
            var t = new Thread(() -> {
                long threadId = Thread.currentThread().getId(); // Get the current thread ID

                try {
                    Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
                    Statement stmt = conn.createStatement();
                    // Execute a sleep query
                    stmt.execute(""); // Sleep for 1 second
                    Thread.sleep(2000);
                    conn.close();

                } catch(Exception e) {
                    System.out.println(e + " "+e.getMessage());
                    System.out.println("Thread unable to get connection");
                }
            });
            t.start();
            threads.add(t);
        }

        for(var thread: threads){
            try {
                thread.join();
            } catch(InterruptedException e){
                System.out.println("Thread join exception");
            }
        }
        var endTime = Instant.now();
        var duration = Duration.between(startTime, endTime);
        System.out.println("Total milliseconds " +duration.toMillis());
    }

    private synchronized void waitAndAdd() throws InterruptedException {
        if(pointer==n) { // this also needs to be atomic
            wait();
        } else {
            pointer += 1; // this should be atomic
        }
    }

    private synchronized void waitAndRemove() throws InterruptedException {
        if(pointer==0){
            wait();
        } else {
            pointer -= 1;
        }
    }

    public void start()  {
        try {
            Class.forName("org.h2.Driver");
            pointer=n;
            System.out.println("Number of threads: "+noOfThreads);
            System.out.println("Connection Pooling using blocking Queue:");
            blockingQueueConnections();
            System.out.println("Creating simultaneous connections");
            System.out.println("Number of threads: "+noOfThreads);
            nonBlockingQueueMaxConnections();
        } catch(Exception e){
            System.out.println("This Exception occurred "+e.getMessage());
        }
    }

    public static void main(String[] args){
        var connectionPooling = new ConnectionPooling();
        connectionPooling.start();
    }

}
