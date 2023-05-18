package com.example.mvcframework.spring;

import com.example.mvcframework.db.DbConfig;
import com.example.mvcframework.db.RouteInfo;
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
    private static Map<String, RouteInfo> controllerMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections(DbConfig.BASE_PACKAGE_PATH);

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                registerMethod(controller, method);
            }
        }
    }

    private static void registerMethod(Class<?> controller, Method method) {
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
            String generalizedMappingValue = generalizeMappingValue(mappingValue);
            controllerMap.put(methodType + ":" + generalizedMappingValue, new RouteInfo(mappingValue, generalizedMappingValue, controller, method));
        }
    }

    private static String generalizeMappingValue(String mappingValue) {
        return mappingValue.replaceAll("\\{.*?}", "{id}");
    }

    public static void runAction(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        String requestMethod = req.getMethod();
        String hiddenMethod = req.getParameter("_method");
        if (hiddenMethod != null && !hiddenMethod.isEmpty()) {
            requestMethod = hiddenMethod.toUpperCase();  // _method 값은 대문자로 변환
        }

        if (requestURI.matches("/usr/article/delete/\\d+")) {
            requestMethod = "DELETE";
        }

        String normalizedRequestURI = normalizeURI(requestURI);
        String key = requestMethod + ":" + normalizedRequestURI;
        RouteInfo routeInfo = controllerMap.get(key);
        if (routeInfo != null) {
            try {
                Object controller = Container.getObj(routeInfo.getControllerCls());
                Rq rq = new Rq(req, resp);
                rq.setRouteInfo(routeInfo);
                routeInfo.getMethod().invoke(controller, rq);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private static String normalizeURI(String uri) {
        String[] uriParts = uri.split("/");
        if (uriParts.length > 0) {
            try {
                Long.parseLong(uriParts[uriParts.length - 1]);
                uriParts[uriParts.length - 1] = "{id}";
            } catch (NumberFormatException ignored) {
            }
        }

        return String.join("/", uriParts);
    }
}




