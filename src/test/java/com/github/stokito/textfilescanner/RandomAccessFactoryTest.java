package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomAccessFactoryTest {

    @Test
    void testFromFileUriFromDataUri() {
        RandomAccessStream randomAccess = RandomAccessFactory.fromFileUri("data:application/octet-stream;base64,MTM4NDcgICAgMjYzMzYgICAgMTkyNjgNCg==");
        assert randomAccess instanceof RandomAccessStringStream;
        RandomAccessStringStream stringStream = (RandomAccessStringStream) randomAccess;
        String line = TextFileStreamReader.inputFileNextLine(stringStream);
        assertEquals("13847    26336    19268", line);
    }

    @Test
    void testFromFileUriFromFile() {
        RandomAccessStream randomAccess = RandomAccessFactory.fromFileUri("src/test/resources/inputdata.txt");
        assert randomAccess instanceof RandomAccessFileStream;
        RandomAccessFileStream stringStream = (RandomAccessFileStream) randomAccess;
        String line = TextFileStreamReader.inputFileNextLine(stringStream);
        assertEquals("input text file example", line);
    }
}
