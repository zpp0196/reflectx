package me.zpp0196.reflectx.demo.test.proguard;

import android.util.LongSparseArray;

import me.zpp0196.reflectx.proxy.IProguardMapping;

public class ProguardManager {

    private static LongSparseArray<IProguardMapping> sMappingMap = new LongSparseArray<>();

    static {
        sMappingMap.append(-1, new DefaultProguardMapping());
    }

    public static IProguardMapping get(long versionCode) {
        IProguardMapping mapping = sMappingMap.get(versionCode);
        if (mapping == null) {
            return sMappingMap.get(-1);
        }
        return mapping;
    }
}
