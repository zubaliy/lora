package com.athome.zubaliy.util;

/**
 * Created by zubaliy on 02/11/14.
 */
public enum AppKey {
    DEVICE_MAC_ADDRESS("device_mac_address"),
    CONFIG_FILENAME("server_local");



    private final String key;


    AppKey(final String key){
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
