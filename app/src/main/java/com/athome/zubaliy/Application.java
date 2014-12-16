package com.athome.zubaliy;

import com.athome.zubaliy.util.Utils;

/**
 * @author Tim Ysewyn
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Utils.terminate();
    }
}
