package com.zganiacz.axwave.client;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Dynamo on 25.11.2016.
 */
public class AudioPacketBuilderTest {

    private static final short TWO_BYTES_OF_DATA = 2;
    private static final short BYTE_OF_DATA = 1;
    private final TimestampProvider timestampProvider = mock(TimestampProvider.class);
    private AudioPacketBuilder tested;

    @Before
    public void before() {
        when(timestampProvider.getTimestamp()).thenReturn(System.currentTimeMillis());
    }

    @Test
    public void shouldBuildHeaderInPacket() throws IOException {
        //given
        tested = new AudioPacketBuilder(TWO_BYTES_OF_DATA, 0, getCode(), timestampProvider);
        InputStream bis = buildAudioInputStream("ANYTHING", TWO_BYTES_OF_DATA);

        //when
        byte[] bytes = tested.buildPacket(bis);

        //then
        byte[] expecteds = getExpectedHeader(TWO_BYTES_OF_DATA);
        assertArrayEquals(expecteds, ArrayUtils.subarray(bytes, 0, AudioPacketBuilder.HEADER_LENGTH));

    }

    @Test
    public void shouldWriteNoRepeats() throws IOException {
        //given
        tested = new AudioPacketBuilder(TWO_BYTES_OF_DATA, 0, getCode(), timestampProvider);
        InputStream bis = buildAudioInputStream("AABBCC", TWO_BYTES_OF_DATA);

        //when, then
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'B', 'B'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'C', 'C'}, TWO_BYTES_OF_DATA);
    }

    @Test
    public void shouldWriteWithRepeats() throws IOException {
        //given
        tested = new AudioPacketBuilder(TWO_BYTES_OF_DATA, BYTE_OF_DATA, getCode(), timestampProvider);
        InputStream bis = buildAudioInputStream("AABBCC", BYTE_OF_DATA);
        bis.mark(0);

        //when, then
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'A', 'B'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'B', 'B'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'B', 'C'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'C', 'C'}, TWO_BYTES_OF_DATA);
    }

    @Test
    public void shouldRepeatIndefinitely() throws IOException {
        //given
        tested = new AudioPacketBuilder(TWO_BYTES_OF_DATA, 2, getCode(), timestampProvider);
        InputStream bis = buildAudioInputStream("AABBCC", TWO_BYTES_OF_DATA);
        bis.mark(0);

        //when, then
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'}, TWO_BYTES_OF_DATA);
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'}, TWO_BYTES_OF_DATA);
    }

    @Test
    public void shouldGuardTheReadDataLength() throws IOException, LineUnavailableException {
        //given
        tested = new AudioPacketBuilder(TWO_BYTES_OF_DATA, 0, getCode(), timestampProvider);
        InputStream bis = buildAudioInputStream("", TWO_BYTES_OF_DATA);
        //when
        try {
            tested.buildPacket(bis);
            fail();
        } catch (IllegalStateException ise) {
            //then exception
            assertTrue(ise.getMessage().contains("Number of bytes read doesn't equal the bytes planned"));
        }
    }

    @Test
    public void shouldThrowOnBadInitializationCases() {
        throwsOnInit(IllegalArgumentException.class, -1, -1);
        throwsOnInit(IllegalArgumentException.class, 0, 0);
        throwsOnInit(IllegalArgumentException.class, 1, -1);
        throwsOnInit(IllegalArgumentException.class, 1, 2);
    }

    private void throwsOnInit(Class c, int length, int repeatLength) {
        try {
            tested = new AudioPacketBuilder(length, repeatLength, getCode(), timestampProvider);
            fail();
        } catch (Exception e) {
            if (!c.isAssignableFrom(c)) fail();

        }
    }

    private void shouldBuildPacketWith(InputStream bis, byte[] array2, short numOfDataBytes) throws IOException {
        byte[] bytes = tested.buildPacket(bis);

        byte[] expecteds = ArrayUtils.addAll(getExpectedHeader(numOfDataBytes), array2);
        assertArrayEquals(expecteds, bytes);
    }


    private Short getCode() {
        return (short) 1;
    }

    private AudioInputStream buildAudioInputStream(String streamContents, short sampleSize) {
        return new AudioInputStream(IOUtils.toInputStream(streamContents, Charset.defaultCharset()), new AudioFormat(1F, sampleSize * 8, 1, false, false), Integer.MAX_VALUE);
    }

    private byte[] getExpectedHeader(short dataSize) {
        byte[] expecteds = ArrayUtils.addAll(AudioPacketBuilder.MAGIC_HEADER_PREFIX, ByteConversionUtils.toBytes((short) (10 + dataSize)));
        expecteds = ArrayUtils.addAll(expecteds, ByteConversionUtils.toBytes(timestampProvider.getTimestamp()));
        expecteds = ArrayUtils.addAll(expecteds, ByteConversionUtils.toBytes(getCode()));
        return expecteds;
    }
}
