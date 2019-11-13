package com.github.stokito.textfilescanner;

import java.io.IOException;

import static java.lang.Math.min;

public interface RandomAccess {
    long length();

    void seek(long position);

    boolean eof();

    /** @return the current position of byte the stream starting from zero so be careful with comparing with length */
    long position();

    default int available() throws IOException {
        if (eof()) {
            return 0;
        }
        long remaining = min(length() - position(), Integer.MAX_VALUE);
        return (int) remaining;
    }

}
