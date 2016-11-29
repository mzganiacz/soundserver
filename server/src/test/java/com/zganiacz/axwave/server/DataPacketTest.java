package com.zganiacz.axwave.server;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import java.io.IOException;

import static com.zganiacz.axwave.server.DataPacket.HEADER_LENGTH;
import static com.zganiacz.axwave.server.DataPacket.MAGIC_HEADER_PREFIX;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Dynamo on 28.11.2016.
 */
public class DataPacketTest {

    public static final short DATA_SIZE = 10;
    public static final long TIMESTAMP = 1234;
    public static final short CODE = 1;
    private DataPacket tested;

    @Test
    public void shouldBuildHeaderInPacket() throws IOException {
        //given

        //when
        tested = new DataPacket(TIMESTAMP, CODE, new byte[DATA_SIZE]);

        //then
        byte[] expecteds = getExpectedHeader(DATA_SIZE);
        assertArrayEquals(expecteds, ArrayUtils.subarray(tested.getPacket(), 0, HEADER_LENGTH));

    }

    private byte[] getExpectedHeader(short dataSize) {
        byte[] expecteds = ArrayUtils.addAll(MAGIC_HEADER_PREFIX, ByteStreamUtilities.toBytes((short) (10 + dataSize)));
        expecteds = ArrayUtils.addAll(expecteds, ByteStreamUtilities.toBytes(1234L));
        expecteds = ArrayUtils.addAll(expecteds, ByteStreamUtilities.toBytes((short) 1));
        return expecteds;
    }

    @Test
    public void shouldGetTimestamp() {
        //given
        tested = new DataPacket(TIMESTAMP, CODE, new byte[DATA_SIZE]);

        //when
        long timestamp = tested.getTimestamp();

        //then
        assertEquals(TIMESTAMP, timestamp);
    }

    @Test
    public void shouldGetFormatCode() {
        //given
        tested = new DataPacket(TIMESTAMP, CODE, new byte[DATA_SIZE]);

        //when
        long code = tested.getFormatCode();

        //then
        assertEquals(CODE, code);
    }

    @Test
    public void shouldGetSample() {
        //given
        byte[] inputSamples = {1, 2, 3, 4, 5, 6, 7, 9, 10};
        tested = new DataPacket(TIMESTAMP, CODE, inputSamples);

        //when
        byte[] samples = tested.getSamples();

        //then
        assertArrayEquals(inputSamples, samples);
    }

}