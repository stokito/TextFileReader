package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomAccessFileStreamTest {

    private final RandomAccessFileStream randomAccessStream = new RandomAccessFileStream("src/test/resources/inputdata.txt");

    @Test
    void testInputFileNextLine() {
        for (int readedBytes = 1; readedBytes <= 141; readedBytes++) {
            int readByte = randomAccessStream.read();
            assertNotEquals(-1, readByte, "readedBytes: " + readedBytes);
            assertFalse(randomAccessStream.eof(), "readedBytes: " + readedBytes);
        }
        int readByte = randomAccessStream.read();
        assertNotEquals(-1, readByte, "last byte");
        assert randomAccessStream.eof() : "last byte";
    }

    @Test
    void eof() throws FileNotFoundException {
        assert !randomAccessStream.eof();
        assertEquals(0, randomAccessStream.position());
        assertEquals(142, randomAccessStream.length());
        randomAccessStream.seek(141);
        char lastCharInFirstLine = (char) randomAccessStream.read();
        assertEquals('\n', lastCharInFirstLine);
        assert randomAccessStream.eof();
        randomAccessStream.seek(141);
        assert !randomAccessStream.eof();
    }

    @Test
    void seek() throws FileNotFoundException {
        assert !randomAccessStream.eof();
        assertEquals(142, randomAccessStream.length());
        randomAccessStream.seek(144);
        assertEquals(142, randomAccessStream.position());
        assert randomAccessStream.eof();
    }
}
