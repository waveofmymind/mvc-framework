package com.example.mvcframework.spring;

import com.example.mvcframework.spring.annotation.Autowired;
import com.example.mvcframework.spring.annotation.Controller;
import com.example.mvcframework.spring.annotation.Repository;
import com.example.mvcframework.spring.annotation.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Container {
    private static Map<Class<?>, Object> instances = new HashMap<>();

    public static <T> T getObj(Class<T> classType) {

        if (instances.containsKey(classType)) {
            return classType.cast(instances.get(classType));
        }

        // Check if classType is annotated with @Service, @Repository, or @Controller
        if (!classType.isAnnotationPresent(Service.class) &&
                !classType.isAnnotationPresent(Repository.class) &&
                !classType.isAnnotationPresent(Controller.class)) {
            throw new RuntimeException(classType.getName() + " does not have @Service, @Repository, or @Controller annotation.");
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

            // Save instance in the map after all dependencies are injected
            instances.put(classType, instance);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void provideObj(Class<T> clazz, T obj) {
        instances.put(clazz, obj);
    }
}

