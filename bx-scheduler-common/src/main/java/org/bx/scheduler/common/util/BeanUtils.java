package org.bx.scheduler.common.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Stream;

public class BeanUtils {
    /**
     * copy proerties exclude excludeFieldName
     *
     * @param src
     * @param dest
     * @param excludeFieldName
     */
    public static void propertyCopy(Object src, Object dest, String... excludeFieldName) {
        Objects.requireNonNull(src, "src object is null");
        Objects.requireNonNull(src, "dest object is null");
        final Class<?> srcClass = src.getClass();
        final Class<?> destClass = dest.getClass();
        final Field[] srcClassDeclaredFields = srcClass.getDeclaredFields();
        final HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(Arrays.asList(excludeFieldName));
        Stream.of(srcClassDeclaredFields).forEach(srcClassField -> {
            final String fieldName = srcClassField.getName();
            if (hashSet.contains(fieldName)) {
                return;
            }
            try {
                final Field destField = destClass.getDeclaredField(fieldName);
                destField.setAccessible(true);
                destField.set(dest, srcClassField.get(src));
            } catch (NoSuchFieldException | IllegalAccessException e) {
            }
        });
    }
}
