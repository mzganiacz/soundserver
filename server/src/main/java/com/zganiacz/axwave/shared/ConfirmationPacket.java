package com.zganiacz.axwave.shared;

import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ConfirmationPacket {

    private final byte[] packet;

    public ConfirmationPacket(long timestamp) {
        packet = ArrayUtils.addAll(DataPacket.MAGIC_HEADER_PREFIX, ByteStreamUtilities.toBytes(timestamp));
    }

    public ConfirmationPacket(byte[] bytes) {
        if (bytes.length != 10) {
            throw new IllegalArgumentException("Confirmation packet is exactly 10 bytes");
        }
        packet = bytes;
    }

    public static ConfirmationPacket readFrom(InputStream is) throws IOException {
        byte[] bytes = new byte[10];
        ByteStreamUtilities.readFullyOrThrow(is, bytes, 0, bytes.length);
        return new ConfirmationPacket(bytes);
    }


    public long getTimestamp() {
        return ByteStreamUtilities.toLong(Arrays.copyOfRange(packet, 2, 10));
    }

    public byte[] getPacket() {
        return packet;
    }
}
