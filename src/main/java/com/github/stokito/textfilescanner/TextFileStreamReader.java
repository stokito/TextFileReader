package com.github.stokito.textfilescanner;

import java.io.IOException;
import java.util.Base64;

import static java.lang.Character.digit;
import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TextFileStreamReader {
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static boolean allowedDataUri = true;
    public static boolean allowedFileUri = true;
    public static boolean allowedStringUri = true;

    public static void inputFileSeekBack(RandomAccessStream inputFileStream) {
        inputFileStream.seek(inputFileStream.position() - 1);
    }

    public static char inputFileReadChar(RandomAccessStream inputFileStream) {
        try {
            return (char) inputFileStream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean eatEoln(RandomAccessStream inputFileStream, char ch) {
        boolean eolnWasEaten;
        // if CR then break cycle, but before we should also read (possible) LF
        if (ch != CR && ch != LF) {
            eolnWasEaten = false;
            return eolnWasEaten;
        }
        // last line can be without CR or CRLF so we can determine the string if EOF was reached
        if (ch == LF || inputFileIsEof(inputFileStream)) {
            eolnWasEaten = true;
            return eolnWasEaten;
        }
        // at least one byte left which can be LF
        char chLf = inputFileReadChar(inputFileStream);
        // if the byte wasn't LF then seek back
        if (chLf != LF) {
            inputFileSeekBack(inputFileStream);
        }
        eolnWasEaten = true;
        return eolnWasEaten;
    }

    public static void eatSpaces(RandomAccessStream inputFileStream) {
        while (!inputFileIsEof(inputFileStream)) {
            char ch = inputFileReadChar(inputFileStream);
            if (!isWhitespace(ch)) {
                inputFileSeekBack(inputFileStream);
                return;
            }
        }
    }

    public static int inputFileLinesCount(String inputFilePath) {
        RandomAccessStream inputFileStream = null;
        try {
            inputFileStream = inputFileOpen(inputFilePath);
            int numberOfLines = 0;
            while (!inputFileIsEof(inputFileStream)) {
                inputFileSkipLn(inputFileStream);
                numberOfLines++;
            }
            return numberOfLines;
        } finally {
            if (inputFileStream != null) {
                inputFileClose(inputFileStream);
            }
        }
    }

    public static RandomAccessStream inputFileOpen(String inputFileUri) {
        if (inputFileUri == null) {
            return null;
        }
        if (inputFileUri.startsWith("data:")) {
            if (!allowedDataUri) {
                throw new RuntimeException("data URI protocol is not allowed");
            }
            // skip "data:application/octet-stream;base64,"
            int contentStartPos = inputFileUri.indexOf(',');
            if (contentStartPos == -1) {
                throw new RuntimeException("Unable to parse data URI");
            }
            String contentBase64 = inputFileUri.substring(contentStartPos + 1);
            String content = new String(Base64.getDecoder().decode(contentBase64), UTF_8);
            return new RandomAccessStringStream(content);
        } else if (inputFileUri.startsWith("file://")) {
            if (!allowedFileUri) {
                throw new RuntimeException("data URI protocol is not allowed");
            }
            String filePath = inputFileUri.substring("file://".length());
            return new RandomAccessFileStream(filePath);
        } else {
            if (!allowedStringUri) {
                throw new RuntimeException("data URI protocol is not allowed");
            }
            return new RandomAccessStringStream(inputFileUri);
        }
    }

    public static RandomAccessStream inputFileFromString(String inputFileContent) {
        RandomAccessStringStream inputFileStream = new RandomAccessStringStream(inputFileContent);
        return inputFileStream;
    }

    public static void inputFileReset(RandomAccessStream inputFileStream) {
        inputFileStream.reset();
    }

    public static void inputFileClose(RandomAccessStream inputFileStream) {
        try {
            inputFileStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String inputFileNextWord(RandomAccessStream inputFileStream) {
        long startPos = inputFileStream.position();
        eatSpaces(inputFileStream);
        assertEof(inputFileStream, startPos);

        String inputVariable = "";
        while (!inputFileIsEoln(inputFileStream)) {
            char ch = inputFileReadChar(inputFileStream);
            if (isWhitespace(ch)) {
                inputFileSeekBack(inputFileStream);
                break;
            }
            inputVariable += ch;
        }
        return inputVariable;
    }

    public static <E extends Enum<E>> E inputFileNextEnum(RandomAccessStream inputFileStream, Class<E> enumClass) {
        String enumName = inputFileNextWord(inputFileStream);
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static String inputFileNextString(RandomAccessStream inputFileStream, int strLength) {
        long leftBytesInStream = inputFileStream.length() - inputFileStream.position() - 1;
        assertCondition(leftBytesInStream > strLength);
        String inputVariable = "";
        long readedBytes = 0;
        while (readedBytes < strLength && !inputFileIsEoln(inputFileStream)) {
            char ch = inputFileReadChar(inputFileStream);
            inputVariable += ch;
            readedBytes++;
        }
        return inputVariable;
    }

    public static String inputFileNextLine(RandomAccessStream inputFileStream) {
        String inputVariable = "";
        while (!inputFileIsEof(inputFileStream)) {
            char ch = inputFileReadChar(inputFileStream);
            if (eatEoln(inputFileStream, ch)) {
                break;
            }
            inputVariable += ch;
        }
        return inputVariable;
    }

    public static boolean inputFileIsEoln(RandomAccessStream inputFileStream) {
        if (inputFileIsEof(inputFileStream)) {
            return true;
        }
        char ch = inputFileReadChar(inputFileStream);
        inputFileSeekBack(inputFileStream);
        return ch == CR || ch == LF;
    }

    public static char inputFileNextChar(RandomAccessStream inputFileStream) {
        assertEof(inputFileStream, inputFileStream.position());
        char ch = inputFileReadChar(inputFileStream);
        return ch;
    }

    public static char inputFileNextSymbol(RandomAccessStream inputFileStream) {
        long startPos = inputFileStream.position();
        eatSpaces(inputFileStream);
        assertEof(inputFileStream, startPos);
        char ch = inputFileReadChar(inputFileStream);
        return ch;
    }

    public static byte inputFileNextDigit(RandomAccessStream inputFileStream) {
        long startPos = inputFileStream.position();
        eatSpaces(inputFileStream);
        assertEof(inputFileStream, startPos);
        char ch = inputFileReadChar(inputFileStream);
        assertCondition(isDigit(ch));
        return (byte) digit(ch, 10);
    }

    public static Integer inputFileNextIntIfExists(RandomAccessStream inputFileStream, boolean raiseException) {
        long startPos = inputFileStream.position();
        eatSpaces(inputFileStream);
        assertEof(inputFileStream, startPos);
        long beginPos = inputFileStream.position();
        String inputValueStr = inputFileNextNumberStr(inputFileStream, false);
        if (inputValueStr.equals("")) {
            if (raiseException) {
                throw new RuntimeException("Unable to parse int startPos: " + startPos + " beginPos: " + beginPos + " endPos: " + inputFileStream.position());
            }
            inputFileStream.seek(startPos);
            return null;
        }
        int inputValue = Integer.parseInt(inputValueStr);
        return inputValue;
    }

    public static Double inputFileNextDoubleIfExists(RandomAccessStream inputFileStream, boolean raiseException) {
        long startPos = inputFileStream.position();
        eatSpaces(inputFileStream);
        assertEof(inputFileStream, startPos);
        long beginPos = inputFileStream.position();
        String inputValueStr = inputFileNextNumberStr(inputFileStream, true);
        if (inputValueStr.equals("")) {
            if (raiseException) {
                throw new RuntimeException("Unable to parse float startPos: " + startPos + " beginPos: " + beginPos + " endPos: " + inputFileStream.position());
            }
            inputFileStream.seek(startPos);
            return null;
        }
        double inputValue = Double.parseDouble(inputValueStr);
        assertCondition(Double.isFinite(inputValue));
        return inputValue;
    }

    public static int inputFileNextInt(RandomAccessStream inputFileStream) {
        return inputFileNextIntIfExists(inputFileStream, true);
    }

    public static double inputFileNextDouble(RandomAccessStream inputFileStream) {
        return inputFileNextDoubleIfExists(inputFileStream, true);
    }

    public static Integer inputFileNextInt(RandomAccessStream inputFileStream, Integer defaultValue) {
        Integer readValue = inputFileNextIntIfExists(inputFileStream, false);
        if (readValue == null) {
            readValue = defaultValue;
        }
        return readValue;
    }

    public static Double inputFileNextDouble(RandomAccessStream inputFileStream, Double defaultValue) {
        Double readValue = inputFileNextDoubleIfExists(inputFileStream, false);
        if (readValue == null) {
            readValue = defaultValue;
        }
        return readValue;
    }

    public static boolean inputFileHasNextInt(RandomAccessStream inputFileStream) {
        return inputFileNextIntIfExists(inputFileStream, false) == null;
    }

    public static boolean inputFileHasNextDouble(RandomAccessStream inputFileStream) {
        return inputFileNextDoubleIfExists(inputFileStream, false) == null;
    }

    public static boolean inputFileHasNextNumber(RandomAccessStream inputFileStream, boolean floatComma) {
        long startPos = inputFileStream.position();
        eatSpaces(inputFileStream);
        assertEof(inputFileStream, startPos);
        String inputValueStr = inputFileNextNumberStr(inputFileStream, floatComma);
        inputFileStream.seek(startPos);
        return inputValueStr.equals("");
    }

    public static String inputFileNextNumberStr(RandomAccessStream inputFileStream, boolean floatComma) {
        String inputValueStr = "";
        while (!inputFileIsEoln(inputFileStream)) {
            char ch = inputFileReadChar(inputFileStream);
            if (ch == '-' || ch == '+' || isDigit(ch)) {
                inputValueStr += ch;
            } else if (floatComma && (ch == '.' || ch == 'E')) {
                inputValueStr += ch;
            } else {
                inputFileSeekBack(inputFileStream);
                break;
            }
        }
        return inputValueStr;
    }

    public static boolean inputFileIsEof(RandomAccessStream inputFileStream) {
        return inputFileStream.eof();
    }

    public static void inputFileSkipLn(RandomAccessStream inputFileStream) {
        inputFileSkipLn(inputFileStream, 1);
    }

    public static void inputFileSkipLn(RandomAccessStream inputFileStream, int linesToSkip) {
        int skippedLines = 0;
        while (!inputFileIsEof(inputFileStream) && skippedLines < linesToSkip) {
            char ch = inputFileReadChar(inputFileStream);
            if (eatEoln(inputFileStream, ch)) {
                skippedLines++;
            }
        }
    }

    public static boolean inputFileHasLines(RandomAccessStream inputFileStream) {
        boolean fileHasSomeNonEmptyLines;
        // check that there is some meaningful and non empty lines left in the file which should be parsed
        long currentPos = inputFileStream.position();
        while (!inputFileIsEof(inputFileStream)) {
            char ch = inputFileReadChar(inputFileStream);
            if (ch != CR && ch != LF && !isWhitespace(ch)) {
                inputFileStream.seek(currentPos);
                fileHasSomeNonEmptyLines = true;
                return fileHasSomeNonEmptyLines;
            }

        }
        fileHasSomeNonEmptyLines = false;
        return fileHasSomeNonEmptyLines;
    }

    public static void inputFileSkipSpaces(RandomAccessStream inputFileStream, int spacesCount) {
        for (int i = 1; i <= spacesCount; i++) {
            char ch = inputFileNextChar(inputFileStream);
            assertCondition(isWhitespace(ch));
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    private static void assertCondition(boolean condition) {
        assertCondition(condition, "Assertion failed");
    }

    private static void assertEof(RandomAccessStream inputFileStream, long startPos) {
        assertCondition(!inputFileIsEof(inputFileStream), "Eof reached startPos: " + startPos);
    }
}
