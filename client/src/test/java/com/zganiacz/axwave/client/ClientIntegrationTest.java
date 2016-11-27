package com.zganiacz.axwave.client;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Dynamo on 24.11.2016.
 */
public class ClientIntegrationTest {

    private Client tested;

    @Test
    public void smokeTest() throws IOException, InterruptedException {
        //given
        tested = new Client(1, 1, null, 1984);

        //when
        final SynchronousQueue<Socket> socketSynchronousQueue = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                socketSynchronousQueue.add(new ServerSocket(1984).accept());
            } catch (IOException e) {

            }
        }).start();
        new Thread(() -> {
            try {
                tested.connectAndSend();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        final Socket socket = socketSynchronousQueue.take();
        byte buff[] = new byte[100000];
        InputStream inputStream = socket.getInputStream();
        while (true) {
            inputStream.read(buff, 0, 100000);

            System.out.println("Yo");
        }

        //then
    }

}