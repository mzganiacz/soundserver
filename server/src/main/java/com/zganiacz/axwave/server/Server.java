package com.zganiacz.axwave.server;

import com.zganiacz.axwave.shared.ConfirmationPacket;
import com.zganiacz.axwave.shared.DataPacket;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {


    private static Logger LOGGER = Logger.getLogger(Server.class.getCanonicalName());
    private final Integer port;
    private final String directory;


    public Server(int port, String directory) {
        this.port = port;
        this.directory = directory;


    }


    public void serve() throws IOException {
        LOGGER.info("Waiting for connection on port " + port);
        Socket clientSocket = new ServerSocket(port).accept();
        LOGGER.info("Got connection from " + clientSocket.getInetAddress());
        ClientConnection clientConnection = new ClientConnection(clientSocket);
        PacketDiskWriter pdw = new PacketDiskWriter(new File(directory));


        while (true) {
            try {
                DataPacket packet = clientConnection.takeIncomingPacket();
                pdw.writePacket(packet);
                clientConnection.confirmAsync(new ConfirmationPacket(packet.getTimestamp()));
            } catch (InterruptedException e) {
                LOGGER.severe("Thread interrupted while waiting to take packet from queue.");
                e.printStackTrace();
                return;
            }
        }

    }


}
