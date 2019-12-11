package me.zpp0196.reflectx.demo.test;

import android.app.Application;
import android.content.Context;

import org.junit.Assert;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.zpp0196.reflectx.android.IProxyMapping;
import me.zpp0196.reflectx.proxy.ProxyClass;
import me.zpp0196.reflectx.proxy.ProxyFactory;
import me.zpp0196.reflectx.proxy.Source;

public class MultiDexLoaderTest implements IXposedHookLoadPackage {

    private static final String CLASS_BUILD_INFO = "org.chromium.base.BuildInfo";
    private static final String METHOD_GET_VERSION = "getVersionName";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        ProxyClass.addMappingClass(IProxyMapping.MAPPING, JavaProxyTest.MAPPING);
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Context context = (Context) param.args[0];
                ClassLoader classLoader = context.getClassLoader();
                Class<?> BuildInfo = XposedHelpers.findClass(CLASS_BUILD_INFO, classLoader);
                Object versionName1 = XposedHelpers.callStaticMethod(BuildInfo, METHOD_GET_VERSION, context);

                ProxyClass.setDefaultClassLoader(classLoader);
                String versionName2 = IBuildInfo.proxy().getVersionName(context);
                Assert.assertEquals(versionName1, versionName2);
            }
        });
    }

    @Source(CLASS_BUILD_INFO)
    public interface IBuildInfo {

        static IBuildInfo proxy() {
            return ProxyFactory.proxyClass(IBuildInfo.class);
        }

        @Source(METHOD_GET_VERSION)
        String getVersionName(Context context);
    }
}
