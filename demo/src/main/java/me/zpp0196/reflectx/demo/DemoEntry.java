package me.zpp0196.reflectx.demo;

import androidx.annotation.Keep;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.zpp0196.reflectx.demo.test.AndroidProxyTest;
import me.zpp0196.reflectx.demo.test.JavaProxyTest;
import me.zpp0196.reflectx.demo.test.MultiDexLoaderTest;

/**
 * @author zpp0196
 */
@Keep
public class DemoEntry implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            if (lpparam.packageName.equals("com.google.android.youtube")) {
                new MultiDexLoaderTest().handleLoadPackage(lpparam);
            }
            return;
        }
        JavaProxyTest.sTestAll();
        AndroidProxyTest.sTestAll();
    }
}
