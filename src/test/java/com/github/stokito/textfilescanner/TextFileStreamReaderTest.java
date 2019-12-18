package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import java.math.RoundingMode;

import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileHasLines;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileHasNextInt;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileLinesCount;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextChar;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextDigit;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextDouble;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextEnum;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextInt;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextIntIfExists;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextLine;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextString;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextSymbol;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileNextWord;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileOpen;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileSeekBack;
import static com.github.stokito.textfilescanner.TextFileStreamReader.inputFileSkipLn;
import static java.math.RoundingMode.CEILING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class TextFileStreamReaderTest {

    public static final String INPUT_FILE_CONTENT = "input text file example\n" +
            "Line \n" +
            "Integer 42 -2147483648 2147483647\n" +
            "  1\t2\n" +
            "    0.25 0.25;\n" +
            "\t1.7976931348623157 -2.2250738585072014\n" +
            "Line1\n" +
            "Line2\n" +
            "Line3\n" +
            "Line4\n" +
            "    \n" +
            "\t\n" +
            "\r" +
            "\r\n" +
            "\n";

    @Test
    void testTextFileStreamReader() {
        RandomAccessStream stream = inputFileOpen(INPUT_FILE_CONTENT);
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
        inputFileSeekBack(stream);
        assertEquals(2, inputFileNextDigit(stream));
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
        inputFileSkipLn(stream);
        inputFileSkipLn(stream, 4);
        assert !inputFileHasLines(stream);
    }

    @Test
    void testLinesCount() {
        int linesCount = inputFileLinesCount("file://src/test/resources/inputdata.txt");
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

    @Test
    void testInputFileNextInt() {
        RandomAccessStream randomAccess = inputFileOpen(" 40");
        int num = inputFileNextInt(randomAccess);
        assertEquals(40, num);
        randomAccess = inputFileOpen(" 2147483647"); // Integer.MAX_VALUE 2147483647
        num = inputFileNextInt(randomAccess);
        assertEquals(Integer.MAX_VALUE, num);
        randomAccess = inputFileOpen(" -2147483648"); // Integer.MIN_VALUE -2147483648
        num = inputFileNextInt(randomAccess);
        assertEquals(Integer.MIN_VALUE, num);
        randomAccess = inputFileOpen(" -1.25"); // float number
        num = inputFileNextInt(randomAccess);
        assertEquals(-1, num);
    }

    @Test
    void testInputFileNextIntIfExists() {
        RandomAccessStream randomAccess = inputFileOpen(" 42 lol");
        Integer num = inputFileNextIntIfExists(randomAccess, false);
        assertEquals(42, num);
        assertEquals(true, inputFileHasNextInt(randomAccess));
        num = inputFileNextIntIfExists(randomAccess, false);
        assertEquals(null, num);
        try {
            inputFileNextIntIfExists(randomAccess, true);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testInputFileNextDigit() {
        RandomAccessStream randomAccess = inputFileOpen(" 12 3 $");
        byte digit = inputFileNextDigit(randomAccess);
        assertEquals(1, digit);
        digit = inputFileNextDigit(randomAccess);
        assertEquals(2, digit);
        digit = inputFileNextDigit(randomAccess);
        assertEquals(3, digit);
        try {
            digit = inputFileNextDigit(randomAccess);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testInputFileNextSymbol() {
        RandomAccessStream randomAccess = inputFileOpen(" 12 3 $");
        char symbol = inputFileNextSymbol(randomAccess);
        assertEquals('1', symbol);
        symbol = inputFileNextSymbol(randomAccess);
        assertEquals('2', symbol);
        symbol = inputFileNextSymbol(randomAccess);
        assertEquals('3', symbol);
        symbol = inputFileNextSymbol(randomAccess);
        assertEquals('$', symbol);
    }

    @Test
    void testInputFileNextDouble() {
        RandomAccessStream randomAccess = inputFileOpen(" 1.7976931348623157E308");
        double num = inputFileNextDouble(randomAccess);
        assertEquals(Double.MAX_VALUE, num);
        randomAccess = inputFileOpen(" 4.9E-324");
        num = inputFileNextDouble(randomAccess);
        assertEquals(Double.MIN_VALUE, num);
    }

    @Test
    void testInputFileNextEnum() {
        RandomAccessStream randomAccess = inputFileOpen(" CEILING OLOLO");
        RoundingMode roundingMode = inputFileNextEnum(randomAccess, RoundingMode.class);
        assertEquals(CEILING, roundingMode);
        roundingMode = inputFileNextEnum(randomAccess, RoundingMode.class);
        assertNull(roundingMode);
    }
}
