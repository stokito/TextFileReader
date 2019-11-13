package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomAccessFileStreamTest {

    @Test
    void eof() throws FileNotFoundException {
        //FIXME use a path to resource
        RandomAccessFileStream randomAccessFileStream = new RandomAccessFileStream("src/test/resources/inputdata.txt");
        assert !randomAccessFileStream.eof();
        assertEquals(0, randomAccessFileStream.position());
        assertEquals(142, randomAccessFileStream.length());
        randomAccessFileStream.seek(22);
        char lastCharInFirstLine = (char) randomAccessFileStream.read();
        assertEquals('e', lastCharInFirstLine);
    }
}
