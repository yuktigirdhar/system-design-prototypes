package com.system.design.prototypes.DatabaseSharding;
import java.sql.Connection;
import java.sql.SQLException;

public class Sharding {
    DataBaseConnection dataBaseConnection;
    IdGenerator idGenerator;

    int noOfThreads = 100;
    Sharding() {
        idGenerator = new IdGenerator();
        dataBaseConnection = new DataBaseConnection();
    }


    private void insertBlogData(Blog blog) {
        String[] insertQueries={"insert into blog (blog_id, author_id, title, content) values (?, ?, ?, ?);"};
        // need to generate a user id
        int blogId = idGenerator.generateId("blog");

        // shard key is author_id
        int shard = getShardIdForUserId(blog.getAuthorId(),2);
        for(var query : insertQueries) {
            try (Connection dbConn = dataBaseConnection.getDbConnection(shard)) { // This one auto closes the connection
                var preparedStatement = dbConn.prepareStatement(query);
                preparedStatement.setInt(1, blogId);
                preparedStatement.setInt(2, blog.getAuthorId());
                preparedStatement.setString(3, blog.getTitle());
                preparedStatement.setString(4, blog.getContent());

                var result = preparedStatement.executeUpdate();

            } catch (SQLException | ClassNotFoundException e) {
                System.out.println("Query threw exception " + e + " message: " + e.getMessage());
            }
        }
    }

    public int getShardIdForUserId(int shardKey, int totalShards) {
        return Math.abs(shardKey) % totalShards;
    }

    // Make this multi-threaded
    private void insertBlogs() {
        var sharding = new Sharding();
        String[][] blogData = {{"1","user1 title","user1 blog content"},
                                {"2","user2 title", "user2 blog content"},
                                {"3","user3 title","user3 blog content"}};
        // 500 threads inserting data into the db
        // 3 users with Id - 1,2,3
        // globally unique ids should be generated and user 1,3 will go to db 2 and user 2 will go to db 1
        for(int i=0;i<noOfThreads;i++) {
            int idx = i % 3;
            var t = new Thread(() -> {
                int authorId = Integer.parseInt(blogData[idx][0]);
                sharding.insertBlogData(new Blog(authorId, blogData[idx][1], blogData[idx][2]));
            });
            t.start();
        }
    }

    public static void main (String[] args) {
        var sharding = new Sharding();
        sharding.insertBlogs();
    }

}
