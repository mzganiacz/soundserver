package com.zganiacz.axwave.server;

import javax.sound.sampled.AudioInputStream;
import java.util.Queue;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class Server {


    public Server(int interval, int packetLengthInSeconds) {
        if (interval < 1 || packetLengthInSeconds < 1) {
            throw new IllegalArgumentException("Both interval and packetLengthInSeconds must equal at least 1 second");
        }

        AudioInputStream audioInputStream = AudioInputDataLineBuilder.buildAudioInputStream();

        int secondLengthInBytes = calcSecondLengthInBytes(audioInputStream);
        int repeatLengthInSeconds = calcRepeatLengthInSeconds(interval, packetLengthInSeconds);


        Queue queue = new Packeter(audioInputStream, interval, packetLengthInSeconds, new AudioPacketBuilder()).startPacketBuilder();


    }

    private int calcSecondLengthInBytes(AudioInputStream audioInputStream) {
        return (int) (audioInputStream.getFormat().getSampleRate() * (audioInputStream.getFormat().getSampleSizeInBits() / 8) * audioInputStream.getFormat().getChannels());
    }

    private int calcRepeatLengthInSeconds(int interval, int dataLength) {
        return dataLength - interval > 0 ? dataLength - interval : 0;
    }
}
