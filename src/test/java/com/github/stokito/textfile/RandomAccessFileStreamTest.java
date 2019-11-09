package com.github.stokito.textfile;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomAccessFileStreamTest {

    @Test
    void eof() throws FileNotFoundException {
        //FIXME use a path to resource
        RandomAccessFileStream randomAccessFileStream = new RandomAccessFileStream("C:\\inputdata.txt");
        assert !randomAccessFileStream.eof();
        assertEquals(0, randomAccessFileStream.position());
        assertEquals(25, randomAccessFileStream.length());
        randomAccessFileStream.seek(22);
        char lastCharInFirstLine = (char) randomAccessFileStream.read();
        assertEquals('e', lastCharInFirstLine);
    }
}
