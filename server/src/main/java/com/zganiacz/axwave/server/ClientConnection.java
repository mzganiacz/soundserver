package com.zganiacz.axwave.server;

import com.zganiacz.axwave.shared.ConfirmationPacket;
import com.zganiacz.axwave.shared.DataPacket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ClientConnection {

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class.getCanonicalName());
    private final Socket socket;
    //bounded as in client - here filling up the receiverQueue will result in back pressing the client
    private final BlockingQueue<DataPacket> receiverQueue = new ArrayBlockingQueue<DataPacket>(100);

    private final BlockingQueue<ConfirmationPacket> confirmationQueue = new ArrayBlockingQueue<ConfirmationPacket>(100);

    public ClientConnection(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Connected socket is expected");
        }
        this.socket = socket;

        new Thread(new ReceiverService()).start();
        new Thread(new ConfirmationService()).start();
    }

    public DataPacket takeIncomingPacket() throws InterruptedException {
        LOGGER.info("Taking packet from receiverQueue, which now has: " + receiverQueue.size() + " elements.");
        return receiverQueue.take();
    }

    public void confirmAsync(ConfirmationPacket confirmationPacket) throws InterruptedException {
        LOGGER.info("Putting packet to confirmationQueue, which now has: " + confirmationQueue.size() + " elements.");
        confirmationQueue.put(confirmationPacket);
    }


    private class ReceiverService implements Runnable {

        public void run() {
            try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream())) {
                while (true) {
                    DataPacket packet = DataPacket.readFrom(bis);
                    LOGGER.info("Putting packet to receiverQueue, which now has: " + receiverQueue.size() + " elements.");
                    receiverQueue.put(packet);
                }
            } catch (IOException e) {
                LOGGER.severe("Problem with receiving data.");
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                LOGGER.severe("ReceiverService interrupted while waiting to put to receiverQueue.");
                e.printStackTrace();
            }
        }

    }

    private class ConfirmationService implements Runnable {

        @Override
        public void run() {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream())) {
                while (true) {
                    LOGGER.info("Taking packet from confirmationQueue, which now has: " + confirmationQueue.size() + " elements.");
                    ConfirmationPacket confirmationPacket = confirmationQueue.take();
                    bufferedOutputStream.write(confirmationPacket.getPacket());
                }
            } catch (IOException e) {
                LOGGER.severe("IOException while working with confirmation socket.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                LOGGER.severe("InterruptedException while waiting to take confirmation packet from confirmationQueue.");
                e.printStackTrace();
            }
        }
    }
}
