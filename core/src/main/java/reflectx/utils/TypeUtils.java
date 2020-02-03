package reflectx.utils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * @author zpp0196
 */
public class TypeUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP_BOX = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP_UNBOX = new HashMap<>();

    static {
        PRIMITIVE_MAP_BOX.put(byte.class, Byte.class);
        PRIMITIVE_MAP_BOX.put(int.class, Integer.class);
        PRIMITIVE_MAP_BOX.put(short.class, Short.class);
        PRIMITIVE_MAP_BOX.put(long.class, Long.class);
        PRIMITIVE_MAP_BOX.put(float.class, Float.class);
        PRIMITIVE_MAP_BOX.put(double.class, Double.class);
        PRIMITIVE_MAP_BOX.put(boolean.class, Boolean.class);
        PRIMITIVE_MAP_BOX.put(char.class, Character.class);

        PRIMITIVE_MAP_UNBOX.put(Byte.class, byte.class);
        PRIMITIVE_MAP_UNBOX.put(Integer.class, int.class);
        PRIMITIVE_MAP_UNBOX.put(Short.class, short.class);
        PRIMITIVE_MAP_UNBOX.put(Long.class, long.class);
        PRIMITIVE_MAP_UNBOX.put(Float.class, float.class);
        PRIMITIVE_MAP_UNBOX.put(Double.class, double.class);
        PRIMITIVE_MAP_UNBOX.put(Boolean.class, boolean.class);
        PRIMITIVE_MAP_UNBOX.put(Character.class, char.class);
    }

    @Nonnull
    public static Class<?> box(@Nonnull Class<?> clazz) {
        Class<?> result = PRIMITIVE_MAP_BOX.get(clazz);
        return result == null ? clazz : result;
    }

    @Nonnull
    public static Class<?> unbox(@Nonnull Class<?> clazz) {
        Class<?> result = PRIMITIVE_MAP_UNBOX.get(clazz);
        return result == null ? clazz : result;
    }
}
