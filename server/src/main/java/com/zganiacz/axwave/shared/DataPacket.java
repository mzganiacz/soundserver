package com.zganiacz.axwave.shared;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.zganiacz.axwave.shared.ByteStreamUtilities.*;

/**
 * This class improves readability of the code, but with cost, because we use it we need to make copies of array instead of working on single instance.
 * That cost is of course negligible.
 */
public class DataPacket {
    public static final byte[] MAGIC_HEADER_PREFIX = new byte[]{0x12, 0x34};
    public static final int HEADER_LENGTH = 14;

    private final byte[] packet;

    public DataPacket(byte[] packet) {
        this.packet = packet;
    }

    public DataPacket(long timestamp, short formatCode, byte[] audioSamples) {
        Integer packetSize = new Integer(audioSamples.length + 10);
        if (packetSize > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Sorry, but packet is too big, max packet size is " + Short.MAX_VALUE + ". Try shorter intervals");
        }
        packet = ByteBuffer.allocate(HEADER_LENGTH + audioSamples.length).
                put(MAGIC_HEADER_PREFIX).
                putShort(packetSize.shortValue()).   //packetSize
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

    public long getTimestamp() {
        return ByteStreamUtilities.toLong(Arrays.copyOfRange(packet, 4, 12));
    }

    public short getFormatCode() {
        return ByteStreamUtilities.toShort(new byte[]{packet[12], packet[13]});
    }

    public byte[] getSamples() {
        return Arrays.copyOfRange(packet, HEADER_LENGTH, packet.length);
    }

    public byte[] getPacket() {
        return packet;
    }


}
