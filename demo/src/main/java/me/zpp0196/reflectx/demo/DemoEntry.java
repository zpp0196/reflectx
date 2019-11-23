package me.zpp0196.reflectx.demo;

import androidx.annotation.Keep;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.zpp0196.reflectx.demo.test.AndroidProxyTest;
import me.zpp0196.reflectx.demo.test.JavaProxyTest;
import me.zpp0196.reflectx.proxy.ProxyClass;

/**
 * @author zpp0196
 */
@Keep
public class DemoEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            return;
        }
        // FIXME: 2019/11/27 0027 ClassLoader
//        ProxyClass.setDefaultClassLoader(lpparam.classLoader);
        JavaProxyTest.sTestAll();
        AndroidProxyTest.sTestAll();
    }
}
