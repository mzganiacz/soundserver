package com.zganiacz.axwave.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class ClientConnection {


    private final Socket socket;
    //the queue is bounded - so in case the client isn't able to drain the queue, and it will fill up,
    //the thread that writes to it will block, and in consequence it won't read the audio stream and the buffer in data
    //line will overflow. This will protect us from OOM, but will result in clicks in sound.
    private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(100);

    public ClientConnection(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Connected socket is expected");
        }
        this.socket = socket;

        new Thread(new SenderService()).start();
    }

    public void sendPacket(byte[] packet) {
        queue.add(packet);
    }

    private class SenderService implements Runnable {

        private Logger logger = Logger.getLogger(SenderService.class.getCanonicalName());

        public void run() {
            while (true) {
                try {
                    takeAndSend();
                } catch (IOException e) {
                    logger.info("Problem with writing to socket. Packet is lost.");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    logger.info("SenderService interrupted while waiting to take from queue. Packet is lost.");
                    e.printStackTrace();
                }
            }
        }

        private void takeAndSend() throws InterruptedException, IOException {
            byte[] toSend = queue.take();
            socket.getOutputStream().write(toSend);
        }
    }
}
