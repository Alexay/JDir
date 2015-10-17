package com.alex;

import java.lang.reflect.Method;
import java.nio.file.attribute.DosFileAttributes;

/**
 * Use this class to determine whether a file is content indexed or not
 */
public class IndexedAttributeReader {
    public static boolean read(DosFileAttributes attr) {
        boolean isIndexed = false;
        if (DosFileAttributes.class.isInstance(attr)) {
            isIndexed = true;
            try {
                Method m = attr.getClass().getDeclaredMethod("attributes");
                m.setAccessible(true);
                int attrs = (int) m.invoke(attr);
                isIndexed = ((attrs & 0x2000) == 0);
            } catch (Exception e) {
                // just gave it a try
            }
        }
        return isIndexed;
    }
}