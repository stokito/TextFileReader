package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import static com.github.stokito.textfilescanner.TextFileStreamReader.*;
import static org.junit.jupiter.api.Assertions.*;

class TextFileStreamReaderTest {

    @Test
    void testTextFileStreamReader() {
        RandomAccessStream stream = inputFileOpen("src/test/resources/inputdata.txt");
        assertEquals('i', inputFileNextChar(stream));
        inputFileSeekBack(stream);
        assertEquals('i', inputFileNextChar(stream));
        assert inputFileHasLines(stream);
        inputFileSkipLn(stream);
        assertEquals("Line ", inputFileNextLine(stream));
        assertEquals("Integer", inputFileNextString(stream, 7));
        assertEquals(42, inputFileNextInt(stream));
        assertEquals(Integer.MIN_VALUE, inputFileNextInt(stream)); // -2147483648
        assertEquals(Integer.MAX_VALUE, inputFileNextInt(stream)); // 2147483647
        inputFileSkipLn(stream);
        assertEquals(1, inputFileNextInt(stream)); // "  1"
        assertEquals(2, inputFileNextInt(stream)); // "\t1"
        inputFileSkipLn(stream);
        assertEquals(0.25D, inputFileNextDouble(stream)); // 0.25
        assertEquals(0.25D, inputFileNextDouble(stream)); // 0.25;
        inputFileSkipLn(stream);
        assertEquals(1.7976931348623157, inputFileNextDouble(stream)); // "\t1.7976931348623157"
        assertEquals(-2.2250738585072014, inputFileNextDouble(stream)); // "\t-2.2250738585072014"
        assert !inputFileHasLines(stream);
    }

    @Test
    void testLinesCount() {
        int linesCount = linesCount("src/test/resources/inputdata.txt");
        assertEquals(9, linesCount);
    }

    @Test
    void testInputFileOpenFromDataUri() {
        RandomAccessStream randomAccess = inputFileOpen("data:application/octet-stream;base64,MTM4NDcgICAgMjYzMzYgICAgMTkyNjgNCg==");
        assert randomAccess instanceof RandomAccessStringStream;
        RandomAccessStringStream stringStream = (RandomAccessStringStream) randomAccess;
        String line = TextFileStreamReader.inputFileNextLine(stringStream);
        assertEquals("13847    26336    19268", line);
    }

    @Test
    void testInputFileOpenFromFile() {
        RandomAccessStream randomAccess = inputFileOpen("src/test/resources/inputdata.txt");
        assert randomAccess instanceof RandomAccessFileStream;
        RandomAccessFileStream stringStream = (RandomAccessFileStream) randomAccess;
        String line = TextFileStreamReader.inputFileNextLine(stringStream);
        assertEquals("input text file example", line);
    }
}
