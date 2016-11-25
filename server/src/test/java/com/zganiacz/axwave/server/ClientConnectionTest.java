package com.zganiacz.axwave.server;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Dynamo on 25.11.2016.
 */
public class ClientConnectionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ClientConnection tested;
    private Socket socketMock;
    private OutputStream osMock;

    @Test
    public void shouldComplainOnUnconnectedSocket() {
        //given
        thrown.expect(IllegalArgumentException.class);
        //when
        tested = new ClientConnection(new Socket());

        //then  exception
    }

    @Test
    public void shouldSendPacketOverSocket() throws IOException {
        //given
        mockSocket();
        tested = new ClientConnection(socketMock);
        //when
        byte[] packet = {'0', '1', '2'};
        tested.sendPacket(packet);

        //then
        verify(osMock).write(eq(packet));
    }

    private void mockSocket() throws IOException {
        socketMock = Mockito.mock(Socket.class);
        osMock = Mockito.mock(OutputStream.class);
        when(socketMock.isConnected()).thenReturn(true);
        when(socketMock.getOutputStream()).thenReturn(osMock);
    }

}