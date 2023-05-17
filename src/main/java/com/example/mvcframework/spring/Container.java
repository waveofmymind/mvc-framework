package com.example.mvcframework.spring;

import com.example.mvcframework.db.ConnectionPool;
import com.example.mvcframework.db.MyMap;
import com.example.mvcframework.spring.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Container {
    private static Map<Class<?>, Object> classMap = new HashMap<>();

    public static <T> T getObj(Class<T> classType) {

        if (classMap.containsKey(classType)) {
            return classType.cast(classMap.get(classType));
        }

        if (!isComponent(classType)) {
            throw new RuntimeException(classType.getName() + ": 컴포넌트 어노테이션이 없습니다.");
        }

        try {
            T instance = classType.getDeclaredConstructor().newInstance();

            for (Field field : classType.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    Object dependency = getObj(fieldType);
                    field.set(instance, dependency);
                }
            }

            classMap.put(classType, instance);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isComponent(Class<?> classType) {
        return classType.isAnnotationPresent(Component.class) ||
                classType.isAnnotationPresent(Service.class) ||
                classType.isAnnotationPresent(Repository.class) ||
                classType.isAnnotationPresent(Controller.class);
    }


    public static <T> void provideObj(Class<T> clazz, T obj) {
        classMap.put(clazz, obj);
    }
}

