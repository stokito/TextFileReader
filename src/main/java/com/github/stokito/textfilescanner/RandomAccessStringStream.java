package com.github.stokito.textfilescanner;

import static java.lang.Math.min;

public class RandomAccessStringStream extends RandomAccessStream {

    private String content;
    private int position;
    private boolean eofReached;

    public RandomAccessStringStream(String content) {
        this.content = content;
    }

    @Override
    public int read() {
        if (eof()) {
            return -1;
        }
        char c = content.charAt(position);
        shiftPosition(position + 1);
        return c;
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public long length() {
        return content.length();
    }

    @Override
    public void seek(long position) {
        shiftPosition(min(position, length() - 1));
    }

    @Override
    public long skip(long n) {
        seek(position() + n);
        return position();
    }

    @Override
    public boolean eof() {
        return eofReached;
    }

    @Override
    public void close() {
        position = 0;
        content = null;
        eofReached = false;
    }

    private void shiftPosition(long newPos) {
        eofReached = position + 1 == length();
        if (!eofReached) {
            position = (int) newPos;
        }
    }
}
