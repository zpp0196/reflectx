package me.zpp0196.reflectx.demo.test.proguard;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import me.zpp0196.reflectx.proxy.IProguardMapping;

public class ProguardMappingFactory implements IProguardMapping {

    private static final String TAG = "ProguardMappingFactory";
    private static ProguardMappingFactory sInstance;

    @NonNull
    private IProguardMapping mMapping;

    public static ProguardMappingFactory getInstance(Context context) {
        if (sInstance == null) {
            long versionCode = getAppVersionCode(context);
            IProguardMapping mapping = ProguardManager.get(versionCode);
            sInstance = new ProguardMappingFactory(mapping);
        }
        return sInstance;
    }

    private ProguardMappingFactory(@NonNull IProguardMapping mapping) {
        this.mMapping = mapping;
    }

    @Override
    public String getSource(@NonNull String name, @NonNull String signature, long hashcode) {
        Log.d(TAG, "getSource() called with: name = [" + name + "], signature = [" + signature + "], hashcode = [" + hashcode + "]");
        return mMapping.getSource(name, signature, hashcode);
    }

    private static long getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return packageInfo.versionCode;
            } else {
                return packageInfo.getLongVersionCode();
            }
        } catch (Throwable ignored) {
            return -1;
        }
    }
}
