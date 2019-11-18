package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import static com.github.stokito.textfilescanner.TextFileStreamReader.*;
import static org.junit.jupiter.api.Assertions.*;

class TextFileStreamReaderTest {

    @Test
    void testTextFileStreamReader() {
        RandomAccessStream stream = inputFileOpen("file://src/test/resources/inputdata.txt");
        assertEquals('i', inputFileNextChar(stream)); // the "i" from "input"
        inputFileSeekBack(stream);
        assertEquals('i', inputFileNextChar(stream)); // again i from input
        assert inputFileHasLines(stream);
        assertEquals("nput", inputFileNextWord(stream));
        assertEquals("text", inputFileNextWord(stream));
        assertEquals('f', inputFileNextSymbol(stream)); // f from " file"
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
        int linesCount = linesCount("file://src/test/resources/inputdata.txt");
        assertEquals(9, linesCount);
    }

    @Test
    void testInputFileNextLine() {
        RandomAccessStringStream inputStream = new RandomAccessStringStream("line");
        String line = inputFileNextLine(inputStream);
        assertEquals("line", line);
    }

    @Test
    void testInputFileOpenFromDataUri() {
        RandomAccessStream randomAccess = inputFileOpen("data:application/octet-stream;base64,MTM4NDcgICAgMjYzMzYgICAgMTkyNjgNCg==");
        assert randomAccess instanceof RandomAccessStringStream;
        RandomAccessStringStream inputStream = (RandomAccessStringStream) randomAccess;
        String line = inputFileNextLine(inputStream);
        assertEquals("13847    26336    19268", line);
    }

    @Test
    void testInputFileOpenFromFile() {
        RandomAccessStream randomAccess = inputFileOpen("file://src/test/resources/inputdata.txt");
        assert randomAccess instanceof RandomAccessFileStream;
        RandomAccessFileStream inputStream = (RandomAccessFileStream) randomAccess;
        String line = inputFileNextLine(inputStream);
        assertEquals("input text file example", line);
    }

    @Test
    void testInputFileOpenFromString() {
        RandomAccessStream randomAccess = inputFileOpen("13847    26336    19268");
        assert randomAccess instanceof RandomAccessStringStream;
        RandomAccessStringStream inputStream = (RandomAccessStringStream) randomAccess;
        String line = inputFileNextLine(inputStream);
        assertEquals("13847    26336    19268", line);
    }
}
