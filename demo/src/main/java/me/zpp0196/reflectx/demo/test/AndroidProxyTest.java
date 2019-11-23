package me.zpp0196.reflectx.demo.test;

import android.app.Application;
import android.util.Log;

import me.zpp0196.reflectx.android.ActivityThread;
import me.zpp0196.reflectx.proxy.ProxyFactory;

/**
 * @author zpp0196
 */
public class AndroidProxyTest {

    private static final String TAG = "AndroidProxyTest";

    public static void sTestAll() {
        AndroidProxyTest test = new AndroidProxyTest();
        test.testAll();
    }

    public void testAll() {
        test();
    }

    public void test() {
        ActivityThread am = ProxyFactory.proxyClass(ActivityThread.class);
        // class android.app.ActivityThread
        log("ActivityThread.class", am);
        am = am.currentActivityThread();
        // android.app.Application@xxxxxxx
        log("ActivityThread.instance", am);
        Application currentApplication = am.currentApplication();
        log("currentApplication", currentApplication);
        String currentPackageName = am.currentPackageName();
        log("currentPackageName", currentPackageName);
        String currentProcessName = am.currentProcessName();
        log("currentProcessName", currentProcessName);
    }

    private void log(String name, Object val) {
        log(name + ": " + val);
    }

    private void log(Object msg) {
        Log.i(TAG, String.valueOf(msg));
    }
}
