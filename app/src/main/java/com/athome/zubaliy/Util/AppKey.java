package com.athome.zubaliy.Util;

import org.androidannotations.annotations.App;

/**
 * Created by zubaliy on 02/11/14.
 */
public enum AppKey {
    DEVICE_MAC_ADDRESS("device_mac_address");



    private final String key;


    AppKey(final String key){
        this.key = key;
    }

    public String getKey(){
        return this.key;
    }
}
