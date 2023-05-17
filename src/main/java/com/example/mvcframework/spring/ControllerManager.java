package com.example.mvcframework.spring;

import com.example.mvcframework.db.DbConfig;
import com.example.mvcframework.db.Rq;
import com.example.mvcframework.spring.annotation.Controller;
import com.example.mvcframework.spring.annotation.mapping.DeleteMapping;
import com.example.mvcframework.spring.annotation.mapping.GetMapping;
import com.example.mvcframework.spring.annotation.mapping.PostMapping;
import com.example.mvcframework.spring.annotation.mapping.PutMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControllerManager {
    private static Map<String, Method> controllerMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections(DbConfig.BASE_PACKAGE_PATH);

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                registerMethod(method);
            }
        }
    }

    private static void registerMethod(Method method) {
        String methodType = null;
        String mappingValue = null;
        if (method.isAnnotationPresent(GetMapping.class)) {
            methodType = "GET";
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            mappingValue = getMapping.value();
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            methodType = "POST";
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            mappingValue = postMapping.value();
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            methodType = "PUT";
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            mappingValue = putMapping.value();
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            methodType = "DELETE";
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            mappingValue = deleteMapping.value();
        }

        if (methodType != null && mappingValue != null) {
            controllerMap.put(methodType + ":" + mappingValue, method);
        }
    }

    public static void runAction(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        String requestMethod = req.getMethod();
        Method method = controllerMap.get(requestMethod + ":" + requestURI);

        if (method != null) {
            try {
                Object controller = Container.getObj(method.getDeclaringClass());

                method.invoke(controller, new Rq(req, resp));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}


