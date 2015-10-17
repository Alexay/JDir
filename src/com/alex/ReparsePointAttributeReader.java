package com.alex;

import java.lang.reflect.Method;
import java.nio.file.attribute.DosFileAttributes;

/**
 *    This class contains a method that allows to correctly parse Windows reparse points and junctions
 */
public class ReparsePointAttributeReader {
    public static boolean read(DosFileAttributes attr) {
        boolean isJunction = false;
        if (DosFileAttributes.class.isInstance(attr)) {
            try {
                Method m = attr.getClass().getDeclaredMethod("isReparsePoint");
                m.setAccessible(true);
                isJunction = (boolean) m.invoke(attr);
            } catch (Exception e) {
                // just gave it a try
            }
        }
        return isJunction;
    }
}