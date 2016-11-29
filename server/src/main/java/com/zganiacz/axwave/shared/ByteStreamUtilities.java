package com.zganiacz.axwave.shared;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class ByteStreamUtilities {


    public static byte[] readFour(InputStream inputStream) throws IOException {
        byte[] b = new byte[4];
        inputStream.read(b, 0, 4);
        return b;
    }

    public static byte[] toBytes(final short i) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(i);
        return bb.array();
    }


    public static short toShort(byte[] coded) {
        if (coded.length != 2) throw new IllegalArgumentException("Expected two bytes");
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(coded);
        bb.flip();
        return bb.getShort();
    }

    public static long toLong(byte[] coded) {
        if (coded.length != 8) throw new IllegalArgumentException("Expected eight bytes");
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.put(coded);
        bb.flip();
        return bb.getLong();
    }

    public static byte[] toBytes(final long i) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(i);
        return bb.array();
    }

    public static void readFullyOrThrow(InputStream bis, byte[] target, int off, int len) throws IOException {
        int read = bis.read(target, off, len);
        if (read != len)
            throw new IllegalStateException("Number of bytes read doesn't equal the bytes planned, is something wrong with streams config? Bytes read " + read + " vs bytes planned " + len);
    }
}
