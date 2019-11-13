package com.github.stokito.textfilescanner;

public class RandomAccessStringStream extends RandomAccessStream {

    private String content;
    private int position;

    public RandomAccessStringStream(String content) {
        this.content = content;
    }

    @Override
    public int read() {
        return content.charAt(position++);
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
        this.position = (int) position;
    }

    @Override
    public long skip(long n) {
        seek(position() + n);
        return position();
    }

    @Override
    public boolean eof() {
        return position() == content.length() - 1;
    }

    @Override
    public void close() {
        content = null;
    }

}
