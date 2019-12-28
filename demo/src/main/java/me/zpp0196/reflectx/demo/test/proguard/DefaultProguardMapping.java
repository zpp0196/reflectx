package me.zpp0196.reflectx.demo.test.proguard;

import androidx.annotation.NonNull;
import me.zpp0196.reflectx.proxy.IProguardMapping;

public class DefaultProguardMapping implements IProguardMapping {
    @Override
    public String getSource(@NonNull String name, @NonNull String signature, long hashcode) {
        return name;
    }
}
