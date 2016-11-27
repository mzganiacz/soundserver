package com.zganiacz.axwave.client;

import javafx.util.Pair;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class Server {


    private static Logger LOGGER = Logger.getLogger(Server.class.getCanonicalName());
    private final int interval;
    private final int packetLengthInSeconds;


    public Server(int interval, int packetLengthInSeconds) {
        if (interval < 1 || packetLengthInSeconds < 1) {
            throw new IllegalArgumentException("Both interval and packetLengthInSeconds must equal at least 1 second");
        }
        this.interval = interval;
        this.packetLengthInSeconds = packetLengthInSeconds;


    }

    public static Pair<AudioFormats.Format, TargetDataLine> tryLines() {
        for (AudioFormats.Format format : AudioFormats.FORMATS.values()) {
            TargetDataLine targetDataLine = null;
            try {
                targetDataLine = AudioSystem.getTargetDataLine(format.getAudioFormat());
                LOGGER.info("Got line " + format.getName());
                return new Pair<>(format, targetDataLine);
            } catch (LineUnavailableException e) {
                LOGGER.info("Unable to get line " + format.getName());
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("Couldnt get any line from system.");

    }

    public void serve() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1984);
        streamAudioToSocket(serverSocket.accept());

    }

    private void streamAudioToSocket(Socket socket) throws IOException {
        Pair<AudioFormats.Format, TargetDataLine> formatAndLine = tryLines();
        AudioFormats.Format format = formatAndLine.getKey();


        AudioInputStream audioInputStream = new AudioInputStream(formatAndLine.getValue());
        BufferedInputStream bis = new BufferedInputStream(audioInputStream);
        bis.mark(0);

        int secondSize = secondSize(format.getAudioFormat());
        int dataSize = secondSize * packetLengthInSeconds;
        int repeatSize = secondSize * calcRepeatLengthInSeconds(interval, packetLengthInSeconds);
        AudioPacketBuilder audioPacketBuilder = new AudioPacketBuilder(dataSize, repeatSize, format.getCode(), new TimestampProvider() {
        });

        ClientConnection cc = new ClientConnection(socket);

        //Pump from in input to socket output
        while (true) {
            byte[] packetContents = audioPacketBuilder.buildPacket(bis);
            cc.sendPacket(packetContents);
        }


    }

    private int secondSize(AudioFormat format) {
        return (int) (format.getSampleRate() * (format.getSampleSizeInBits() / 8) * format.getChannels());
    }

    private int calcRepeatLengthInSeconds(int interval, int dataLength) {
        return dataLength - interval > 0 ? dataLength - interval : 0;
    }


}
