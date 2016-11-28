package com.zganiacz.axwave.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.zganiacz.axwave.server.ByteStreamUtilities.*;

/**
 * Created by Dynamo on 28.11.2016.
 */
public class DataPacket {
    public static final byte[] MAGIC_HEADER_PREFIX = new byte[]{0x12, 0x34};
    public static final int HEADER_LENGTH = 14;

    private final byte[] packet;

    public DataPacket(byte[] packet) {
        this.packet = packet;
    }

    public DataPacket(long timestamp, short formatCode, byte[] audioSamples) {
        packet = ByteBuffer.allocate(HEADER_LENGTH + audioSamples.length).
                put(MAGIC_HEADER_PREFIX).
                putShort((short) (audioSamples.length + 10)).   //packetSize
                putLong(timestamp).       //timestamp of first sample
                putShort(formatCode).     //soundFormat
                put(audioSamples).
                array();
    }

    /*Guaranteed to read only enough bytes to build single packet*/
    public static DataPacket readFrom(InputStream inputStream) throws IOException {
        byte[] prefixAndSize = readFour(inputStream);
        if (notAMagicHeader(Arrays.copyOf(prefixAndSize, 2))) {
            throw new IllegalArgumentException("Expected magic prefix at the head of the stream, but got something else");
        }
        int toRead = readSize(Arrays.copyOfRange(prefixAndSize, 2, 4));
        byte[] packet = Arrays.copyOf(prefixAndSize, 4 + toRead);
        readFullyOrThrow(inputStream, packet, 4, toRead);
        return new DataPacket(packet);
    }

    private static short readSize(byte[] size) throws IOException {
        return toShort(size);
    }

    private static boolean notAMagicHeader(byte[] maybeMagicHeader) throws IOException {
        return toShort(maybeMagicHeader) != toShort(MAGIC_HEADER_PREFIX);
    }

    public byte[] getPacket() {
        return packet;
    }


}
