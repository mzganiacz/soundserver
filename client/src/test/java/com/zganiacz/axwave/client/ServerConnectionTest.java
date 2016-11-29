package com.zganiacz.axwave.client;

import com.zganiacz.axwave.shared.DataPacket;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

public class ServerConnectionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ServerConnection tested;
    private Socket socketMock;
    private PipedInputStream socketSink;

    @Test
    public void shouldComplainOnUnconnectedSocket() {
        //given
        thrown.expect(IllegalArgumentException.class);
        //when
        tested = new ServerConnection(new Socket());

        //then  exception
    }

    @Test
    public void shouldSendPacketOverSocket() throws IOException, InterruptedException {
        //given
        mockSocket();
        tested = new ServerConnection(socketMock);
        //when
        byte[] packet = {'0', '1', '2'};
        tested.sendPacketAsync(new DataPacket(packet));
        byte[] read = new byte[3];
        socketSink.read(read);

        //then
        assertArrayEquals(packet, read);
    }


    private void mockSocket() throws IOException {
        socketMock = Mockito.mock(Socket.class);
        when(socketMock.isConnected()).thenReturn(true);
        socketSink = new PipedInputStream();
        when(socketMock.getOutputStream()).thenReturn(new PipedOutputStream(socketSink));
        when(socketMock.getInputStream()).thenReturn(new PipedInputStream(new PipedOutputStream()));
    }

}