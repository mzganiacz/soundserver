package com.zganiacz.axwave.client;

import com.zganiacz.axwave.server.DataPacket;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Created by Dynamo on 25.11.2016.
 */
public class DataPacketBuilderTest {

    private static final short TWO_BYTES_OF_DATA = 2;
    private static final short BYTE_OF_DATA = 1;
    private static final int TIMESTAMP = 1234;
    private static final short FORMAT_CODE = 1;

    private DataPacketBuilder tested;


    @Test
    public void shouldWriteNoRepeats() throws IOException {
        //given
        tested = new DataPacketBuilder(TWO_BYTES_OF_DATA, 0);
        InputStream bis = buildAudioInputStream("AABBCC", TWO_BYTES_OF_DATA);

        //when, then
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'});
        shouldBuildPacketWith(bis, new byte[]{'B', 'B'});
        shouldBuildPacketWith(bis, new byte[]{'C', 'C'});
    }

    @Test
    public void shouldWriteWithRepeats() throws IOException {
        //given
        tested = new DataPacketBuilder(TWO_BYTES_OF_DATA, BYTE_OF_DATA);
        InputStream bis = buildAudioInputStream("AABBCC", BYTE_OF_DATA);
        bis.mark(0);

        //when, then
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'});
        shouldBuildPacketWith(bis, new byte[]{'A', 'B'});
        shouldBuildPacketWith(bis, new byte[]{'B', 'B'});
        shouldBuildPacketWith(bis, new byte[]{'B', 'C'});
        shouldBuildPacketWith(bis, new byte[]{'C', 'C'});
    }

    @Test
    public void shouldRepeatIndefinitely() throws IOException {
        //given
        tested = new DataPacketBuilder(TWO_BYTES_OF_DATA, 2);
        InputStream bis = buildAudioInputStream("AABBCC", TWO_BYTES_OF_DATA);
        bis.mark(0);

        //when, then
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'});
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'});
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'});
        shouldBuildPacketWith(bis, new byte[]{'A', 'A'});
    }

    @Test
    public void shouldGuardTheReadDataLength() throws IOException, LineUnavailableException {
        //given
        tested = new DataPacketBuilder(TWO_BYTES_OF_DATA, 0);
        InputStream bis = buildAudioInputStream("", TWO_BYTES_OF_DATA);
        //when
        try {
            tested.buildPacket(bis, TIMESTAMP, FORMAT_CODE);
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
            tested = new DataPacketBuilder(length, repeatLength);
            fail();
        } catch (Exception e) {
            if (!c.isAssignableFrom(c)) fail();

        }
    }

    private void shouldBuildPacketWith(InputStream bis, byte[] expected) throws IOException {
        DataPacket bytes = tested.buildPacket(bis, TIMESTAMP, FORMAT_CODE);

        assertArrayEquals(expected, bytes.getSamples());
    }


    private InputStream buildAudioInputStream(String streamContents, short sampleSize) {
        return new BufferedInputStream(new AudioInputStream(IOUtils.toInputStream(streamContents, Charset.defaultCharset()), new AudioFormat(1F, sampleSize * 8, 1, false, false), Integer.MAX_VALUE));
    }


}
