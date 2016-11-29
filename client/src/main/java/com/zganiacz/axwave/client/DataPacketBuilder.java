package com.zganiacz.axwave.client;

import com.zganiacz.axwave.server.DataPacket;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import static com.zganiacz.axwave.server.ByteStreamUtilities.readFullyOrThrow;

/**
 * For skae of improving performance (a bit) it would be better if during BytePacket creation
 * we wouldn't create intermediate byte array here and write to bute array in DataPacket instance once. That would mean
 * that this builder must be internal class of DataPacket. On the other hand, having it here as separate class is more
 * readable
 */
public class DataPacketBuilder {

    private static Logger LOGGER = Logger.getLogger(DataPacketBuilder.class.getCanonicalName());
    private final int repeatLength;
    private final int length;

    public DataPacketBuilder(int length, int repeatLength) {
        if (repeatLength > length) throw new IllegalArgumentException("Repeat length cant be greater than length");
        if (length < 1 || repeatLength < 0)
            throw new IllegalArgumentException("Length must be at least 1 and repeat length can't be negative");
        this.repeatLength = repeatLength;
        this.length = length;
    }

    public DataPacket buildPacket(InputStream is, long timestamp, short formatCode) throws IOException {
        LOGGER.info(String.format("Building DataPacket. Length: %d, RepeatLength: %d", length, repeatLength));
        byte[] packet = new byte[length];
        if (repeatLength > 0) {
            is.reset();
            readFullyOrThrow(is, packet, 0, length - repeatLength);
            is.mark(repeatLength);
            readFullyOrThrow(is, packet, length - repeatLength, repeatLength);

        } else {
            readFullyOrThrow(is, packet, 0, this.length);
        }
        return new DataPacket(timestamp, formatCode, packet);
    }


}
