package com.github.stokito.textfilescanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomAccessStringStreamTest {
    private RandomAccessStringStream stringStream = new RandomAccessStringStream("0123456789");

    @Test
    void eof() {
        assert !stringStream.eof();
        stringStream.seek(9);
        assert stringStream.eof();
    }

    @Test
    void length() {
        assert stringStream.length() == 10;
    }

    @Test
    void seek() {
        stringStream.seek(0);
        assertEquals(0, stringStream.position());
        stringStream.seek(9);
        assertEquals(9, stringStream.position());
    }

    @Test
    void read() {
        for (int i = 0; i < 10; i++) {
            int b = stringStream.read();
            assert Character.forDigit(i, 10) == (char) b;
        }
    }
}
