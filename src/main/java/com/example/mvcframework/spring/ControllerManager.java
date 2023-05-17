package com.example.mvcframework.spring;

import com.example.mvcframework.db.Rq;
import com.example.mvcframework.spring.annotation.Controller;
import com.example.mvcframework.spring.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControllerManager {
    private static Map<String, Method> getMappingMethods = new HashMap<>();

    static {
        Reflections reflections = new Reflections("com.example.mvcframework");

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    getMappingMethods.put(getMapping.value(), method);
                }
            }
        }
    }

    public static void runAction(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        Method method = getMappingMethods.get(requestURI);

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

