package com.zganiacz.axwave.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PacketDiskWriter {

    private final File dir;

    public PacketDiskWriter(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Expected a directory");
        }
        this.dir = dir;
    }

    public void writePacket(DataPacket packet) {
        try {
            openStreamAndWrite(packet, prepareFile(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStreamAndWrite(DataPacket packet, File fileToSavePacketTo) throws IOException {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileToSavePacketTo))) {
            bufferedOutputStream.write(packet.getSamples());
            bufferedOutputStream.flush();
        }
    }


    private File prepareFile(DataPacket packet) throws IOException {
        Path path = buildFileName(packet, getFormat(packet));
        if (Files.exists(path)) {
            throw new IllegalStateException("Got  filename conflict. File is not saved. File: " + path.toAbsolutePath());
        }
        Files.createDirectories(path.getParent());

        return Files.createFile(path).toFile();
    }

    private AudioFormats.Format getFormat(DataPacket packet) {
        AudioFormats.Format format = AudioFormats.FORMATS.get(packet.getFormatCode());
        if (format == null) {
            throw new IllegalStateException("Got a packet with unknown audio format.");
        }
        return format;
    }

    private Path buildFileName(DataPacket packet, AudioFormats.Format format) {
        return Paths.get(dir.getAbsolutePath() + File.separator + format.getName() + File.separator + (packet.getTimestamp() + ".pcm"));
    }
}
