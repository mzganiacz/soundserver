package com.zganiacz.axwave.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class ServerConnection {


    private static Logger LOGGER = Logger.getLogger(ServerConnection.class.getCanonicalName());
    private final Socket socket;
    //the queue is bounded - so in case the client isn't able to drain the queue, and it will fill up,
    //the thread that writes to it will block, and in consequence it won't read the audio stream and the buffer in data
    //line will overflow. This will protect us from OOM, but will result in clicks in sound.
    private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(100);

    public ServerConnection(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Connected socket is expected");
        }
        this.socket = socket;

        new Thread(new SenderService()).start();
    }

    public void sendPacket(byte[] packet) {
        try {
            LOGGER.info("Putting packet on queue, which now has: " + queue.size() + " elements.");
            queue.put(packet);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted exception while waiting to put on the processing queue.");
            e.printStackTrace();
        }
    }


    private class SenderService implements Runnable {

        public void run() {
            try {
                while (true) {
                    takeAndSend();
                }
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.severe("Problem with closing the socket.");
                    e.printStackTrace();
                }
            }
        }

        private void takeAndSend() {
            try {
                LOGGER.info("Taking packet from queue, which now has: " + queue.size() + " elements.");
                byte[] toSend = queue.take();
                socket.getOutputStream().write(toSend);
            } catch (IOException e) {
                LOGGER.severe("Problem with writing to socket. Packet is lost.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                LOGGER.severe("SenderService interrupted while waiting to take from queue. Packet is lost.");
                e.printStackTrace();
            }
        }

    }
}
