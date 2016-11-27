package com.zganiacz.axwave.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class Server {


    private static Logger LOGGER = Logger.getLogger(Server.class.getCanonicalName());
    private final Integer port;


    public Server(int port) {
        this.port = port;


    }


    public void serve() throws IOException {
        Socket clientSocket = new ServerSocket(port).accept();
        ClientConnection clientConnection = new ClientConnection(clientSocket);


        while (true) {
            try {
                byte[] bytes = clientConnection.takePacket();


            } catch (InterruptedException e) {
                LOGGER.severe("Thread interrupted while waiting to take packet from queue.");
                e.printStackTrace();
            }
        }

    }


}
