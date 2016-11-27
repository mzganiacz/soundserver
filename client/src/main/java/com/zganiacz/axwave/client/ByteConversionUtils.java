package com.zganiacz.axwave.client;

import java.nio.ByteBuffer;

/**
 * Created by Dynamo on 25.11.2016.
 */
public class ByteConversionUtils {

    public static byte[] toBytes(final short i) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(i);
        return bb.array();
    }


    public static short toShort(byte[] coded) {
        if (coded.length != 2) throw new IllegalArgumentException("Expected two bytes");
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(coded);
        return bb.getShort();
    }

    public static byte[] toBytes(final long i) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(i);
        return bb.array();
    }
}
