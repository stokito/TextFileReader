package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomAccessFileStreamTest {

    @Test
    void testInputFileNextLine() {
        RandomAccessFileStream randomAccessFileStream = new RandomAccessFileStream("src/test/resources/inputdata.txt");
        for (int readedBytes = 1; readedBytes <= 141; readedBytes++) {
            int readByte = randomAccessFileStream.read();
            assertNotEquals(-1, readByte, "readedBytes: " + readedBytes);
            assertFalse(randomAccessFileStream.eof(), "readedBytes: " + readedBytes);
        }
        int readByte = randomAccessFileStream.read();
        assertNotEquals(-1, readByte, "last byte");
        assert randomAccessFileStream.eof() : "last byte";
    }

    @Test
    void eof() throws FileNotFoundException {
        RandomAccessFileStream randomAccessFileStream = new RandomAccessFileStream("src/test/resources/inputdata.txt");
        assert !randomAccessFileStream.eof();
        assertEquals(0, randomAccessFileStream.position());
        assertEquals(142, randomAccessFileStream.length());
        randomAccessFileStream.seek(22);
        char lastCharInFirstLine = (char) randomAccessFileStream.read();
        assertEquals('e', lastCharInFirstLine);
    }

    @Test
    void seek() throws FileNotFoundException {
        RandomAccessFileStream randomAccessFileStream = new RandomAccessFileStream("src/test/resources/inputdata.txt");
        assert !randomAccessFileStream.eof();
        assertEquals(142, randomAccessFileStream.length());
        randomAccessFileStream.seek(144);
    }
}
