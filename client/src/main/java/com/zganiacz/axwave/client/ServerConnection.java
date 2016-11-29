package com.zganiacz.axwave.client;

import com.zganiacz.axwave.shared.ConfirmationPacket;
import com.zganiacz.axwave.shared.DataPacket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ServerConnection {


    private static Logger LOGGER = Logger.getLogger(ServerConnection.class.getCanonicalName());
    private final Socket socket;
    //the sendingQueue is bounded - so in case the client isn't able to drain the sendingQueue, and it will fill up,
    //the thread that writes to it will block, and in consequence it won't read the audio stream and the buffer in data
    //line will overflow. This will protect us from OOM, but will result in clicks in sound.
    private final BlockingQueue<DataPacket> sendingQueue = new ArrayBlockingQueue<DataPacket>(100);

    public ServerConnection(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Connected socket is expected");
        }
        this.socket = socket;

        new Thread(new SenderService()).start();
        new Thread(new ReceiverService()).start();
    }

    public void sendPacketAsync(DataPacket packet) {
        try {
            sendingQueue.put(packet);
            LOGGER.info("Put packet on sendingQueue, which now has: " + sendingQueue.size() + " elements.");
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted exception while waiting to put on the processing sendingQueue.");
            e.printStackTrace();
        }
    }


    private class SenderService implements Runnable {

        public void run() {
            try (OutputStream outputStream = socket.getOutputStream()) {
                while (true) {
                    byte[] toSend = sendingQueue.take().getPacket();
                    LOGGER.info("Took packet from sendingQueue, which now has: " + sendingQueue.size() + " elements.");
                    outputStream.write(toSend);
                }
            } catch (IOException e) {
                LOGGER.severe("Problem with writing to socket.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                LOGGER.severe("SenderService interrupted while waiting to take from sendingQueue.");
                e.printStackTrace();
            }

        }
    }

    private class ReceiverService implements Runnable {

        public void run() {
            try (InputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
                while (true) {
                    LOGGER.info("Got confirmation on packet with timestamp " + ConfirmationPacket.readFrom(inputStream).getTimestamp());
                }
            } catch (IOException e) {
                LOGGER.severe("Problem with writing to socket.");
                e.printStackTrace();
            }

        }
    }


}
