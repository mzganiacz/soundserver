package com.zganiacz.axwave.server;

import com.zganiacz.axwave.shared.DataPacket;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;

public class PacketDiskWriterTest {

    public static final Path SAMPLES_DIR = Paths.get(new File(PacketDiskWriter.class.getResource("/").getPath() + File.separator + "audioSamples").getAbsolutePath());
    private PacketDiskWriter tested;
    private FileInputStream fileInputStream;

    @Before
    public void before() throws IOException {
        Files.createDirectories(SAMPLES_DIR);
    }

    @After
    public void after() throws IOException {
        fileInputStream.close();
        FileUtils.forceDelete(SAMPLES_DIR.toFile());
    }

    @Test
    public void shouldWritePacket() throws IOException {
        //given
        tested = new PacketDiskWriter(SAMPLES_DIR.toFile());
        final byte[] audioSamples = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        //when
        tested.writePacket(new DataPacket(1234, Short.valueOf("1"), audioSamples));
        //then
        byte[] read = new byte[10];
        fileInputStream = new FileInputStream(SAMPLES_DIR.toFile().getAbsolutePath() + File.separator + "S8kHz8bitMono" + File.separator + "1234.pcm");
        fileInputStream.read(read);
        assertArrayEquals(audioSamples, read);
    }
}