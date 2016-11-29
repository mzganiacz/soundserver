package com.zganiacz.axwave.client;

import com.zganiacz.axwave.server.DataPacket;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ServerConnectionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ServerConnection tested;
    private Socket socketMock;
    private OutputStream osMock;

    @Test
    public void shouldComplainOnUnconnectedSocket() {
        //given
        thrown.expect(IllegalArgumentException.class);
        //when
        tested = new ServerConnection(new Socket(), Executors.newSingleThreadExecutor());

        //then  exception
    }

    @Test
    public void shouldSendPacketOverSocket() throws IOException, InterruptedException {
        //given
        mockSocket();
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        tested = new ServerConnection(socketMock, executor);
        //when
        byte[] packet = {'0', '1', '2'};
        tested.sendPacketAsync(new DataPacket(packet));
        executor.awaitTermination(1, TimeUnit.SECONDS);

        //then
        Mockito.verify(osMock).write(eq(packet));
    }


    private void mockSocket() throws IOException {
        socketMock = Mockito.mock(Socket.class);
        osMock = Mockito.mock(OutputStream.class);
        when(socketMock.isConnected()).thenReturn(true);
        when(socketMock.getOutputStream()).thenReturn(osMock);
    }

}