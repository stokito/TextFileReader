package com.github.stokito.textfile;

import java.io.InputStream;

/**
 * InputStream with random access and seeking.
 */
public abstract class RandomAccessStream extends InputStream implements RandomAccess {

    private Long mark;

    @Override
    public final boolean markSupported() {
        return true;
    }

    @Override
    public final synchronized void mark(int readLimit) {
        mark = position();
    }

    @Override
    public synchronized void reset() {
        seek(mark != null ? mark : 0);
    }
}
