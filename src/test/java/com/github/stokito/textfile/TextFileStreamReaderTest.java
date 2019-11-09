package com.github.stokito.textfile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextFileStreamReaderTest {

    @Test
    void testTextFileStreamReader() {
        RandomAccessStream stream = TextFileStreamReader.inputFileOpen("src/test/resources/inputdata.txt");
        assertEquals('i', TextFileStreamReader.inputFileNextChar(stream));
        TextFileStreamReader.inputFileSeekBack(stream);
        assertEquals('i', TextFileStreamReader.inputFileNextChar(stream));
        assert TextFileStreamReader.inputFileHasLines(stream);
        TextFileStreamReader.inputFileSkipLn(stream);
        assertEquals("Integer", TextFileStreamReader.inputFileNextString(stream, 7));
        assertEquals(42, TextFileStreamReader.inputFileNextInt(stream));
        assertEquals(Integer.MIN_VALUE, TextFileStreamReader.inputFileNextInt(stream)); // -2147483648
        assertEquals(Integer.MAX_VALUE, TextFileStreamReader.inputFileNextInt(stream)); // 2147483647
        TextFileStreamReader.inputFileSkipLn(stream);
        assertEquals(1, TextFileStreamReader.inputFileNextInt(stream)); // "  1"
        assertEquals(2, TextFileStreamReader.inputFileNextInt(stream)); // "\t1"
        TextFileStreamReader.inputFileSkipLn(stream);
        assertEquals(0.25D, TextFileStreamReader.inputFileNextDouble(stream)); // 0.25
        assertEquals(0.25D, TextFileStreamReader.inputFileNextDouble(stream)); // 0.25;
        TextFileStreamReader.inputFileSkipLn(stream);
        assertEquals(1.7976931348623157, TextFileStreamReader.inputFileNextDouble(stream)); // "\t1.7976931348623157"
        assertEquals(-2.2250738585072014, TextFileStreamReader.inputFileNextDouble(stream)); // "\t-2.2250738585072014"
        assert !TextFileStreamReader.inputFileHasLines(stream);
    }

    @Test
    void linesCount() {
        int linesCount = TextFileStreamReader.linesCount("src/test/resources/inputdata.txt");
        assertEquals(10, linesCount);
    }
}
