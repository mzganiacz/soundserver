package com.zganiacz.axwave.shared;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfirmationPacketTest {

    @Test
    public void shouldGetTimestamp() {
        //given
        ConfirmationPacket confirmationPacket = new ConfirmationPacket(1234L);
        //when
        long timestamp = confirmationPacket.getTimestamp();
        //then
        assertEquals(1234L, timestamp);
    }

}