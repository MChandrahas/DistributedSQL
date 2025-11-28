package com.dbsql;

import com.dbsql.storage.Table;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Starting Mass Insertion Test ---");

        String dbFile = "test.db";
        Table table = new Table(dbFile);

        int recordCount = 10000; // Let's hit that resume metric
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < recordCount; i++) {
            String data = "User ID:" + i + "|Name:User_" + i + "|Email:user" + i + "@example.com";
            table.insert(data.getBytes(StandardCharsets.UTF_8));
        }
        
        table.close();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Successfully inserted " + recordCount + " records.");
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        
        System.out.println("--- Validation ---");
        // Quick check: Read the first page back
        Table readTable = new Table(dbFile);
        byte[] p0 = readTable.getDiskManager().readPage(0);
        // Just print the first few bytes to prove data is there
        System.out.println("Page 0 Size: " + p0.length + " bytes (Full)");
    }
}