package com.zganiacz.axwave.client;

import com.zganiacz.axwave.shared.AudioFormats;
import com.zganiacz.axwave.shared.DataPacket;
import javafx.util.Pair;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {


    private static Logger LOGGER = Logger.getLogger(Client.class.getCanonicalName());
    private final int interval;
    private final int packetLengthInSeconds;
    private final String host;
    private final Integer port;


    public Client(int interval, int packetLengthInSeconds, String host, Integer port) {
        if (interval < 1 || packetLengthInSeconds < 1) {
            throw new IllegalArgumentException("Both interval and packetLengthInSeconds must equal at least 1 second");
        }
        this.interval = interval;
        this.packetLengthInSeconds = packetLengthInSeconds;
        this.host = host;
        this.port = port;


    }

    private static Pair<AudioFormats.Format, TargetDataLine> findAvailableAudioInputLine() {
        for (AudioFormats.Format format : AudioFormats.FORMATS.values()) {
            TargetDataLine targetDataLine = null;
            try {
                targetDataLine = AudioSystem.getTargetDataLine(format.getAudioFormat());
                targetDataLine.open();
                targetDataLine.start();
                LOGGER.info("Got line " + format.getName());
                return new Pair<>(format, targetDataLine);
            } catch (LineUnavailableException e) {
                LOGGER.info("Unable to get line " + format.getName());
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("Couldnt get any line from system.");

    }

    public void connectAndSend() throws IOException {
        Socket clientSocket = new Socket(host, port);
        streamAudioToSocket(clientSocket);

    }

    private void streamAudioToSocket(Socket socket) throws IOException {
        ServerConnection sc = new ServerConnection(socket);

        Pair<AudioFormats.Format, TargetDataLine> formatAndLine = findAvailableAudioInputLine();
        AudioFormats.Format format = formatAndLine.getKey();


        try (AudioInputStream audioInputStream = new AudioInputStream(formatAndLine.getValue())) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);
            bufferedInputStream.mark(0);

            int secondSize = secondSize(format.getAudioFormat());
            int dataSize = secondSize * packetLengthInSeconds;
            int repeatSize = secondSize * calcRepeatLengthInSeconds(interval, packetLengthInSeconds);
            DataPacketBuilder dataPacketBuilder = new DataPacketBuilder(dataSize, repeatSize);

            //Pump from in input to socket output
            while (true) {
                DataPacket packet = dataPacketBuilder.buildPacket(bufferedInputStream, System.currentTimeMillis(), formatAndLine.getKey().getCode());
                sc.sendPacketAsync(packet);
            }
        }


    }

    private int secondSize(AudioFormat format) {
        return (int) (format.getSampleRate() * (format.getSampleSizeInBits() / 8) * format.getChannels());
    }

    private int calcRepeatLengthInSeconds(int interval, int dataLength) {
        return dataLength - interval > 0 ? dataLength - interval : 0;
    }


}
