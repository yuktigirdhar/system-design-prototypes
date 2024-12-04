# Problem Statement

`For a blogging application generate globally unique IDs for each blog and each user`

### Design overview
- I have a blogs database which can have a million blogs in it, 
- The database is sharded on user id so that all blogs belonging to same user are local to one database  
- A separate file is created to store the counter for each table, with pattern directory/tableName 

## Setup

1. A SQL Database setup on host localhost:3306
2. Run the queries in blog_db_queries.sql file to create the tables
3Replace the directory path where the counter file is stored for recovery when API server: sdDirectory variable in IdGenerator class