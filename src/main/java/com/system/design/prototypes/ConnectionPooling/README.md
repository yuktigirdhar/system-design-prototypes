# Connection Pool Benchmarking in Java


## Features
Implements connection pooling with the help of a blocking queue, for concurrent processes and threads to get access to database connections 


## Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/yuktigirdhar/system-design-prototypes.git
    ```

2. **Install maven dependencies**

    ```bash
      mvn clean install
    ```

## Running the Program
    Run the main method in ConnectionPooling.java class 

## Tweaking some parameters
    In the ConnectionPooling.java class you can change variables:

    1. n: Total number of connections in the queue for threads to use
    2. noOfThreads: Total number of threads to run the test on
