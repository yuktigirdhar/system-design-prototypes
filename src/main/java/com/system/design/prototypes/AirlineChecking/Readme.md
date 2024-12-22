### Problem Statement

`` Handling multiple people trying to check in. A multi threaded scenario where there are  
    120 seats and all the 120 people who have booked the flight are trying to check in, ``


## Motive

`` Testing database locks``

1. Particularly testing the FOR UPDATE locks provided by DB to handle contention
   2. SELECT * ..... FOR UPDATE;
   3. SELECT * ........ FOR UPDATE SKIP LOCKED;


## Run

1. Run the airline.sql file queries first
2. Then in the main method uncheck the methods **airlineDAO.generateFlight();** and **airlineDAO.generateUsers()** and run them only once if you are running the program multiple times
3. Then update the **booking.simulateUsersBooking(3);** with the approach 1, 2,3 for the non lock and for update, for update skip locked methods respectively.