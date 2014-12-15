package com.athome.zubaliy.util;

/**
 * Created by zubaliy on 02/11/14.
 */
public enum AppKey {
    DEVICE_MAC_ADDRESS("device_mac_address"),
    CONFIG_LINK_SERVER_LOCAL("server_local"),
    CONFIG_LINK_SERVER_WEB("server_web"),

    SHORT_JOURNEY("short_journey"),
    SHORT_BREAK("short_break");



    private final String key;


    AppKey(final String key){
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
