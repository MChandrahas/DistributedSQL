package com.dbsql.storage;

import java.nio.ByteBuffer;

public class SlottedPage {
    private final byte[] data;
    private final ByteBuffer buffer;

    // Header size: 4 bytes for the "number of slots" count
    private static final int HEADER_SIZE = 4;
    // Slot size: 4 bytes for offset + 4 bytes for length = 8 bytes
    private static final int SLOT_SIZE = 8;

    public SlottedPage(byte[] data) {
        if (data.length != Constants.PAGE_SIZE) {
            throw new IllegalArgumentException("Page must be " + Constants.PAGE_SIZE + " bytes");
        }
        this.data = data;
        this.buffer = ByteBuffer.wrap(data);
    }

    /**
     * @return The number of records stored in this page.
     */
    public int getNumSlots() {
        return buffer.getInt(0); // Read the first 4 bytes
    }

    /**
     * @return The remaining free space in bytes.
     */
    public int getFreeSpace() {
        int numSlots = getNumSlots();
        int headerEnd = HEADER_SIZE + (numSlots * SLOT_SIZE);
        
        // Find the start of the data (or end of page if empty)
        int dataStart = Constants.PAGE_SIZE;
        if (numSlots > 0) {
            // The last slot points to the most recently added data (lowest offset)
            // Note: In a real DB, we'd scan all slots to find the lowest offset. 
            // Simplified here: assuming strictly sequential inserts for now.
            int lastSlotOffset = HEADER_SIZE + ((numSlots - 1) * SLOT_SIZE);
            dataStart = buffer.getInt(lastSlotOffset); 
        }
        
        return dataStart - headerEnd;
    }

    /**
     * Inserts a record into the page.
     * @param record The byte array of the row.
     * @return The slot ID (index) where it was stored, or -1 if full.
     */
    public int insertRecord(byte[] record) {
        int freeSpace = getFreeSpace();
        int spaceNeeded = SLOT_SIZE + record.length; // We need space for the data AND the slot pointer

        if (spaceNeeded > freeSpace) {
            return -1; // Page is full
        }

        int numSlots = getNumSlots();
        
        // 1. Calculate where to put the data (growing from the end)
        int dataStart;
        if (numSlots == 0) {
            dataStart = Constants.PAGE_SIZE;
        } else {
             int lastSlotOffset = HEADER_SIZE + ((numSlots - 1) * SLOT_SIZE);
             dataStart = buffer.getInt(lastSlotOffset);
        }
        
        int insertOffset = dataStart - record.length;

        // 2. Write the data
        buffer.position(insertOffset);
        buffer.put(record);

        // 3. Update the Slot Array (growing from the front)
        int currentSlotOffset = HEADER_SIZE + (numSlots * SLOT_SIZE);
        buffer.putInt(currentSlotOffset, insertOffset); // Store Offset
        buffer.putInt(currentSlotOffset + 4, record.length); // Store Length

        // 4. Update the Header (increment record count)
        buffer.putInt(0, numSlots + 1);

        return numSlots; // Return the index of the new record
    }

    /**
     * Read a record by its slot ID.
     */
    public byte[] getRecord(int slotId) {
        int numSlots = getNumSlots();
        if (slotId < 0 || slotId >= numSlots) {
            throw new IllegalArgumentException("Invalid slot ID");
        }

        int slotOffset = HEADER_SIZE + (slotId * SLOT_SIZE);
        int recordOffset = buffer.getInt(slotOffset);
        int recordLength = buffer.getInt(slotOffset + 4);

        byte[] record = new byte[recordLength];
        // Save current position
        int oldPos = buffer.position();
        
        buffer.position(recordOffset);
        buffer.get(record);
        
        // Restore position
        buffer.position(oldPos);
        
        return record;
    }

    // Returns the raw page data to be saved to disk
    public byte[] toBytes() {
        return data;
    }
}