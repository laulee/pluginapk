package com.laulee.pluginlib;

import java.lang.reflect.Field;

/**
 * Created by laulee on 2020-03-06.
 */
public class RefInvoke {

    public static void setFieldObject(Object object, String name, Object newResources) {
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, newResources);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getFieldObject(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
