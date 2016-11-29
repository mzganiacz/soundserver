package com.zganiacz.axwave.server;

import com.zganiacz.axwave.shared.DataPacket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class ClientConnection {




    private static Logger LOGGER = Logger.getLogger(ClientConnection.class.getCanonicalName());
    private final Socket socket;
    //bounded as in client - here filling up the queue will result in back pressing the client
    private final BlockingQueue<DataPacket> queue = new ArrayBlockingQueue<DataPacket>(100);

    public ClientConnection(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Connected socket is expected");
        }
        this.socket = socket;

        new Thread(new ReceiverService()).start();
    }

    public DataPacket takeIncomingPacket() throws InterruptedException {
        LOGGER.info("Taking packet from queue, which now has: " + queue.size() + " elements.");
        return queue.take();

    }


    private class ReceiverService implements Runnable {

        public void run() {
            try {
                reveivePackets();
            } catch (IOException e) {
                LOGGER.severe("Problem with receiving data. Server wont work.");
                e.printStackTrace();
                return;
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.severe("Problem with closing the socket.");
                    e.printStackTrace();
                }
            }
        }

        private void reveivePackets() throws IOException {
            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);

            while (true) {
                try {
                    DataPacket packet = DataPacket.readFrom(bis);
                    LOGGER.info("Putting packet to queue, which now has: " + queue.size() + " elements.");
                    queue.put(packet);
                } catch (InterruptedException e) {
                    LOGGER.severe("ReceiverService interrupted while waiting to put to queue. Packet is lost.");
                    e.printStackTrace();
                }
            }
        }




    }
}
