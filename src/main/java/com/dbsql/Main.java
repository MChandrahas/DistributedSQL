package com.dbsql;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- DistributedSQL Engine ---");
        
        DatabaseEngine db = new DatabaseEngine("mydb.db");
        
        // 1. Insert Data
        System.out.println("Executing Insert...");
        db.execute("INSERT INTO users VALUES (1, \"Chandrahas\")");
        db.execute("INSERT INTO users VALUES (2, \"Alice\")");
        db.execute("INSERT INTO users VALUES (3, \"Bob\")");

        // 2. Select Data
        System.out.println("\nExecuting Select...");
        String result = db.execute("SELECT * FROM users");
        System.out.println(result);
        
        db.close();
    }
}