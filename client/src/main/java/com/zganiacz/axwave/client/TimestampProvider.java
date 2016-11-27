package com.zganiacz.axwave.client;

/**
 * Created by Dynamo on 25.11.2016.
 */
public interface TimestampProvider {

    default long getTimestamp() {
        return System.currentTimeMillis();
    }
}
