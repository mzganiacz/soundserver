package com.zganiacz.axwave.server;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import java.io.IOException;

import static com.zganiacz.axwave.server.DataPacket.HEADER_LENGTH;
import static com.zganiacz.axwave.server.DataPacket.MAGIC_HEADER_PREFIX;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Dynamo on 28.11.2016.
 */
public class DataPacketTest {

    private DataPacket tested;


    @Test
    public void shouldBuildHeaderInPacket() throws IOException {
        //given
        final short dataSize = 10;
        final long timestamp = 1234;
        final short code = 1;

        //when
        tested = new DataPacket(timestamp, code, new byte[dataSize]);

        //then
        byte[] expecteds = getExpectedHeader(dataSize);
        assertArrayEquals(expecteds, ArrayUtils.subarray(tested.getPacket(), 0, HEADER_LENGTH));

    }

    private byte[] getExpectedHeader(short dataSize) {
        byte[] expecteds = ArrayUtils.addAll(MAGIC_HEADER_PREFIX, ByteStreamUtilities.toBytes((short) (10 + dataSize)));
        expecteds = ArrayUtils.addAll(expecteds, ByteStreamUtilities.toBytes(1234L));
        expecteds = ArrayUtils.addAll(expecteds, ByteStreamUtilities.toBytes((short) 1));
        return expecteds;
    }

}