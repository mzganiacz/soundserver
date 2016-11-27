package com.zganiacz.axwave.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static com.zganiacz.axwave.server.ByteStreamUtilities.*;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class ClientConnection {

    public static final byte[] MAGIC_HEADER_PREFIX = new byte[]{0x12, 0x34};
    public static final int HEADER_LENGTH = 14;


    private static Logger LOGGER = Logger.getLogger(ClientConnection.class.getCanonicalName());
    private final Socket socket;
    //bounded as in client - here filling up the queue will result in back pressing the client
    private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(100);

    public ClientConnection(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Connected socket is expected");
        }
        this.socket = socket;

        new Thread(new ReceiverService()).start();
    }

    public byte[] takePacket() throws InterruptedException {
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
                    byte[] packet = readWholePacket(bis);
                    LOGGER.info("Putting packet to queue, which now has: " + queue.size() + " elements.");
                    queue.put(packet);
                } catch (InterruptedException e) {
                    LOGGER.severe("ReceiverService interrupted while waiting to put to queue. Packet is lost.");
                    e.printStackTrace();
                }
            }
        }

        private byte[] readWholePacket(InputStream inputStream) throws IOException {
            while (notAMagicHeader(inputStream)) ;
            int toRead = readSize(inputStream);
            byte[] packet = new byte[toRead];
            readFullyOrThrow(inputStream, packet, 0, toRead);
            return packet;
        }

        private short readSize(InputStream inputStream) throws IOException {
            return toShort(readTwo(inputStream));
        }

        private boolean notAMagicHeader(InputStream inputStream) throws IOException {
            return toShort(readTwo(inputStream)) != toShort(MAGIC_HEADER_PREFIX);
        }

    }
}
