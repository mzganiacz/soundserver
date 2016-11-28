package com.zganiacz.axwave.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import static com.zganiacz.axwave.server.ByteStreamUtilities.readFullyOrThrow;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class AudioSamplesBuilder {


    private static Logger LOGGER = Logger.getLogger(AudioSamplesBuilder.class.getCanonicalName());
    private final int repeatLength;
    private final int length;

    public AudioSamplesBuilder(int length, int repeatLength) {
        if (repeatLength > length) throw new IllegalArgumentException("Repeat length cant be greater than length");
        if (length < 1 || repeatLength < 0)
            throw new IllegalArgumentException("Length must be at least 1 and repeat length can't be negative");
        this.repeatLength = repeatLength;
        this.length = length;
    }


    public byte[] buildPacket(InputStream is) throws IOException {
        LOGGER.info(String.format("Building audio samples. Length: %d, RepeatLength: %d", length, repeatLength));
        byte[] packet = new byte[length];
        if (repeatLength > 0) {
            is.reset();
            readFullyOrThrow(is, packet, 0, length - repeatLength);
            is.mark(repeatLength);
            readFullyOrThrow(is, packet, length - repeatLength, repeatLength);

        } else {
            readFullyOrThrow(is, packet, 0, this.length);
        }
        return packet;
    }


}
