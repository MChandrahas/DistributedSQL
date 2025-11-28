package com.dbsql;

import com.dbsql.storage.Constants;
import com.dbsql.storage.DiskManager;
import com.dbsql.storage.SlottedPage;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Starting DistributedSQL Page Layout Test ---");

        String dbFile = "test.db";
        DiskManager diskManager = new DiskManager(dbFile);

        // 1. Create a new empty page
        byte[] rawPage = new byte[Constants.PAGE_SIZE];
        SlottedPage page = new SlottedPage(rawPage);

        // 2. Insert User 1
        String user1 = "ID:1|Name:Chandrahas|Role:Admin";
        int slot1 = page.insertRecord(user1.getBytes(StandardCharsets.UTF_8));
        System.out.println("Inserted User 1 at Slot: " + slot1);

        // 3. Insert User 2
        String user2 = "ID:2|Name:Alice|Role:User";
        int slot2 = page.insertRecord(user2.getBytes(StandardCharsets.UTF_8));
        System.out.println("Inserted User 2 at Slot: " + slot2);

        // 4. Save to Disk (Page 0)
        diskManager.writePage(0, page.toBytes());
        diskManager.close();

        System.out.println("Database closed. Re-opening...");

        // 5. Read Back
        DiskManager readManager = new DiskManager(dbFile);
        byte[] loadedRawPage = readManager.readPage(0);
        SlottedPage loadedPage = new SlottedPage(loadedRawPage);

        // 6. Decode Data
        System.out.println("Records in page: " + loadedPage.getNumSlots());
        
        String rec1 = new String(loadedPage.getRecord(0));
        System.out.println("Read Slot 0: " + rec1);
        
        String rec2 = new String(loadedPage.getRecord(1));
        System.out.println("Read Slot 1: " + rec2);
        
        System.out.println("Free Space Remaining: " + loadedPage.getFreeSpace() + " bytes");
    }
}