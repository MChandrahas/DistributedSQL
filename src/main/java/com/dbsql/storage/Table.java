package com.dbsql.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final DiskManager diskManager;
    private int currentPageId;
    private SlottedPage currentPage;

    public Table(String dbFile) {
        this.diskManager = new DiskManager(dbFile);
        
        // RECOVERY LOGIC: Figure out where we left off
        try {
            long fileSize = diskManager.getFileSize();
            if (fileSize > 0) {
                // Page IDs are 0-indexed. If size is 8192 (2 pages), last ID is 1.
                this.currentPageId = (int) (fileSize / Constants.PAGE_SIZE) - 1;
                byte[] raw = diskManager.readPage(currentPageId);
                this.currentPage = new SlottedPage(raw);
            } else {
                this.currentPageId = 0;
                this.currentPage = new SlottedPage(new byte[Constants.PAGE_SIZE]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize table", e);
        }
    }

    public void insert(byte[] record) {
        int slotId = currentPage.insertRecord(record);
        if (slotId == -1) {
            flushCurrentPage();
            currentPageId++;
            currentPage = new SlottedPage(new byte[Constants.PAGE_SIZE]);
            currentPage.insertRecord(record);
        }
    }

    private void flushCurrentPage() {
        diskManager.writePage(currentPageId, currentPage.toBytes());
    }

    public void close() {
        flushCurrentPage();
        diskManager.close();
    }

    /**
     * FULL TABLE SCAN: Reads every page, every slot.
     */
    public List<String> scan() {
        List<String> results = new ArrayList<>();
        
        // Loop through all pages from 0 to current
        for (int i = 0; i <= currentPageId; i++) {
            // Load the page from disk
            byte[] rawPage = diskManager.readPage(i);
            SlottedPage page = new SlottedPage(rawPage);
            
            // Loop through all slots in the page
            int numSlots = page.getNumSlots();
            for (int s = 0; s < numSlots; s++) {
                byte[] record = page.getRecord(s);
                results.add(new String(record, StandardCharsets.UTF_8));
            }
        }
        return results;
    }
}