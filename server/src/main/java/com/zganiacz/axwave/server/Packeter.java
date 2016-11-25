package com.zganiacz.axwave.server;

import java.io.InputStream;
import java.util.Queue;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class Packeter {
    private Queue packetQueue;


    public Packeter(InputStream audioInputStream, int interval, int packetLengthInSeconds, AudioPacketBuilder audioPacketBuilder) {

    }

    public Queue startPacketBuilder() {
        return packetQueue;
    }
}
