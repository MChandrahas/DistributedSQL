package com.dbsql.storage;

import java.io.IOException;

public class Table {
    private final DiskManager diskManager;
    private int currentPageId;
    private SlottedPage currentPage;

    public Table(String dbFile) {
        this.diskManager = new DiskManager(dbFile);
        this.currentPageId = 0;
        
        // Try to load page 0, or create it if new
        try {
            // Check if file has data, otherwise start fresh
            byte[] raw = diskManager.readPage(0);
            this.currentPage = new SlottedPage(raw);
        } catch (Exception e) {
            // File likely empty or new, create fresh page
            this.currentPage = new SlottedPage(new byte[Constants.PAGE_SIZE]);
        }
    }

    /**
     * Insert a record into the table. 
     * If the current page is full, it creates a new one.
     */
    public void insert(byte[] record) {
        int slotId = currentPage.insertRecord(record);

        // If slotId is -1, the page is full!
        if (slotId == -1) {
            flushCurrentPage(); // Save the full page to disk
            
            // Move to next page
            currentPageId++; 
            
            // Create a fresh page in memory
            currentPage = new SlottedPage(new byte[Constants.PAGE_SIZE]);
            
            // Try insert again on the new page
            currentPage.insertRecord(record);
        }
    }

    private void flushCurrentPage() {
        diskManager.writePage(currentPageId, currentPage.toBytes());
    }

    public void close() {
        flushCurrentPage(); // Ensure the last bit of data is saved
        diskManager.close();
    }
    
    // Helper to read raw data for testing
    public DiskManager getDiskManager() {
        return diskManager;
    }
}