package com.zganiacz.axwave.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class AudioPacketBuilder {

    public static final byte[] MAGIC_HEADER_PREFIX = new byte[]{0x12, 0x34};
    public static final int HEADER_LENGTH = 14;
    private static Logger LOGGER = Logger.getLogger(AudioPacketBuilder.class.getCanonicalName());
    private final int repeatLength;
    private final int length;
    private final TimestampProvider timestampProvider;
    private final short formatCode;

    public AudioPacketBuilder(int length, int repeatLength, short formatCode, TimestampProvider timestampProvider) {
        if (repeatLength > length) throw new IllegalArgumentException("Repeat length cant be greater than length");
        if (length < 1 || repeatLength < 0)
            throw new IllegalArgumentException("Length must be at least 1 and repeat length can't be negative");
        this.formatCode = formatCode;
        this.repeatLength = repeatLength;
        this.length = length;
        this.timestampProvider = timestampProvider;
    }

    private static void readFullyOrThrow(InputStream bis, byte[] target, int off, int len) throws IOException {
        int read = bis.read(target, off, len);
        if (read != len)
            throw new IllegalStateException("Number of bytes read doesn't equal the bytes planned, is something wrong with streams config? Bytes read " + read + " vs bytes planned " + len);
    }

    public byte[] buildPacket(InputStream is) throws IOException {
        LOGGER.info(String.format("Building packet. Length: %d, RepeatLength: %d", length, repeatLength));
        byte[] packet = new byte[HEADER_LENGTH + length];
        writeHeader(packet);
        if (repeatLength > 0) {
            is.reset();
            readFullyOrThrow(is, packet, HEADER_LENGTH, length - repeatLength);
            is.mark(repeatLength);
            readFullyOrThrow(is, packet, HEADER_LENGTH + length - repeatLength, repeatLength);

        } else {
            readFullyOrThrow(is, packet, HEADER_LENGTH, this.length);

        }
        return packet;
    }

    private void writeHeader(byte[] packetContents) {
        ByteBuffer bf = ByteBuffer.allocate(HEADER_LENGTH);
        byte[] headerArray = bf.put(MAGIC_HEADER_PREFIX).
                putShort((short) (packetContents.length - 4)).   //packetSize
                putLong(timestampProvider.getTimestamp()).       //timestamp of first sample
                putShort(formatCode).array();                    //soundFormat
        for (int i = 0; i < headerArray.length; i++) {
            packetContents[i] = headerArray[i];
        }
    }


}
