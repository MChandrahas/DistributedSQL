package com.dbsql.storage;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskManager {
    private final String dbFileName;
    private RandomAccessFile dbFile;

    public DiskManager(String file) {
        this.dbFileName = file;
        try {
            // "rw" means Read and Write permissions
            this.dbFile = new RandomAccessFile(file, "rw");
        } catch (IOException e) {
            throw new RuntimeException("Cannot open database file: " + file);
        }
    }

    /**
     * Write a page to the database file.
     * @param pageId The specific page number (0, 1, 2...)
     * @param pageData The raw bytes (must be 4096 bytes)
     */
    public void writePage(int pageId, byte[] pageData) {
        if (pageData.length != Constants.PAGE_SIZE) {
            throw new IllegalArgumentException("Data must be exactly " + Constants.PAGE_SIZE + " bytes");
        }
        try {
            int offset = pageId * Constants.PAGE_SIZE;
            dbFile.seek(offset); // Jump to the specific spot in the file
            dbFile.write(pageData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write page " + pageId, e);
        }
    }

    /**
     * Read a page from the database file.
     * @param pageId The specific page number
     * @return 4096 bytes of data
     */
    public byte[] readPage(int pageId) {
        byte[] buffer = new byte[Constants.PAGE_SIZE];
        try {
            int offset = pageId * Constants.PAGE_SIZE;
            dbFile.seek(offset); // Jump to the specific spot
            dbFile.readFully(buffer); // Fill the buffer with data from disk
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read page " + pageId, e);
        }
    }

    public void close() {
        try {
            dbFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getFileSize() throws IOException {
        return dbFile.length();
    }
}