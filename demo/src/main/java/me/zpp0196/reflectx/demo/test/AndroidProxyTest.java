package me.zpp0196.reflectx.demo.test;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import org.junit.Assert;

import java.lang.reflect.Proxy;

import me.zpp0196.reflectx.demo.proxy.IProxyMapping;
import proxy.android.app.ActivityManager;
import proxy.android.app.ActivityManagerNative;
import proxy.android.app.ActivityThread;
import proxy.android.app.IActivityManager;
import proxy.android.util.Singleton;
import me.zpp0196.reflectx.demo.proxy.IBuild;
import me.zpp0196.reflectx.proxy.ProxyClass;
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
        ProxyClass.addMappingClass(IProxyMapping.MAPPING);
        testActivityThread();
        testProxyActivityManager();
        testSetFinal();
    }

    public void testSetFinal() {
        String newBrand = Build.BRAND + "New";
        IBuild.proxy().setBrand(newBrand);
        Assert.assertEquals(newBrand, Build.BRAND);
    }

    public void testActivityThread() {
        ActivityThread am = ActivityThread.proxy();
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

    public void testProxyActivityManager() {
        Singleton singleton;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityManagerNative am = ProxyFactory.proxyClass(ActivityManagerNative.class);
            singleton = am.gDefault();
        } else {
            ActivityManager am = ProxyFactory.proxyClass(ActivityManager.class);
            singleton = am.IActivityManagerSingleton();
        }
        Object instance = singleton.getInstance();
        log("ActivityManager", instance);
        singleton.setInstance(getProxyActivityManager(instance));
    }

    private Object getProxyActivityManager(Object original) {
        ClassLoader classLoader = original.getClass().getClassLoader();
        Class[] interfaces = {ProxyClass.findClass(IActivityManager.class)};
        return Proxy.newProxyInstance(classLoader, interfaces,
                (proxy, method, args) -> {
                    log("ActivityManager.invoke", method.getName());
                    try {
                        return method.invoke(original, args);
                    } catch (Throwable th) {
                        if (th.getCause() != null) {
                            throw th.getCause();
                        }
                        throw th;
                    }
                });
    }

    private void log(String name, Object val) {
        log(name + ": " + val);
    }

    private void log(Object msg) {
        Log.i(TAG, String.valueOf(msg));
    }
}
