package com.github.stokito.textfile;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileStream extends RandomAccessStream {

    private RandomAccessFile file;

    public RandomAccessFileStream(String fileName) {
        try {
            file = new RandomAccessFile(fileName, "r");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean eof() {
        try {
            return file.getFilePointer() == file.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long position() {
        try {
            return file.getChannel().position();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long length() {
        try {
            return file.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void seek(long position) {
        try {
            file.seek(position);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        try {
            return file.read(buffer, offset, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read() {
        try {
            return file.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read(byte[] b) {
        try {
            return file.read(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file = null;
    }

}
