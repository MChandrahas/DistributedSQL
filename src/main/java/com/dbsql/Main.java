package com.dbsql;

import com.dbsql.storage.Constants;
import com.dbsql.storage.DiskManager;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Starting DistributedSQL Storage Test ---");
        
        String dbFile = "test.db";
        DiskManager diskManager = new DiskManager(dbFile);

        // 1. Create dummy data
        String message = "Hello, this is a persistent database record!";
        byte[] rawData = message.getBytes(StandardCharsets.UTF_8);
        
        // 2. Fit it into a 4KB Page
        byte[] page0Data = new byte[Constants.PAGE_SIZE];
        System.arraycopy(rawData, 0, page0Data, 0, rawData.length);

        // 3. Write to Disk (Page 0)
        System.out.println("Writing to Page 0...");
        diskManager.writePage(0, page0Data);

        // 4. Write something else to Page 5 (simulating a large DB)
        byte[] page5Data = new byte[Constants.PAGE_SIZE];
        String msg2 = "I am far away in Page 5";
        System.arraycopy(msg2.getBytes(), 0, page5Data, 0, msg2.getBytes().length);
        System.out.println("Writing to Page 5...");
        diskManager.writePage(5, page5Data);
        
        // 5. Close and Re-open (Simulate a restart)
        diskManager.close();
        System.out.println("Database closed. Re-opening...");
        
        DiskManager readManager = new DiskManager(dbFile);
        
        // 6. Read back Page 0
        byte[] readBack = readManager.readPage(0);
        String decoded = new String(readBack, StandardCharsets.UTF_8).trim();
        System.out.println("Read from Page 0: " + decoded);

        // 7. Read back Page 5
        byte[] readBack5 = readManager.readPage(5);
        String decoded5 = new String(readBack5, StandardCharsets.UTF_8).trim();
        System.out.println("Read from Page 5: " + decoded5);
    }
}