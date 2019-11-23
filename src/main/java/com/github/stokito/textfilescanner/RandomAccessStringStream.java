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
        seek(position + 1);
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
        this.position = (int) min(position, length());
        eofReached = this.position == length();
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

}
