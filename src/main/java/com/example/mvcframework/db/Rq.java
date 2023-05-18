package com.example.mvcframework.db;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    @Setter
    @Getter
    private RouteInfo routeInfo;

    public Rq(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;

        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=utf-8");
    }

    public String getPathParam(String paramName, String defaultValue) {
        if (routeInfo == null) {
            return defaultValue;
        }

        String path = routeInfo.getPath();

        String[] pathSplitList = path.split("/");

        Map<String, Integer> paramIndexMap = new HashMap<>();

        for (int i = 0; i < pathSplitList.length; i++) {
            String pathBit = pathSplitList[i];

            if (pathBit.startsWith("{") && pathBit.endsWith("}")) {
                String name = pathBit.substring(1, pathBit.length() - 1);
                paramIndexMap.put(name, i);
            }
        }

        Integer index = paramIndexMap.get(paramName);

        if (index != null) {
            return getPathValueByIndex(index, defaultValue);
        }

        return defaultValue;
    }

    public String getPathValueByIndex(int index, String defaultValue) {
        String[] pathSplitList = req.getRequestURI().split("/");

        try {
            return pathSplitList[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return defaultValue;
        }
    }


    public String getParam(String paramName, String defaultValue) {
        String value = req.getParameter(paramName);

        if ( value == null ) {
            value = getPathParam(paramName, null);
        }

        if (value == null || value.trim().length() == 0) {
            return defaultValue;
        }

        return value;
    }

    public long getLongParam(String paramName, long defaultValue) {
        String value = getParam(paramName, null);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getIntParam(String paramName, int defaultValue) {
        String value = getParam(paramName, null);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void print(String str) {
        try {
            resp.getWriter().append(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void println(String str) {
        print(str + "\n");
    }

    public void setAttr(String name, Object value) {
        req.setAttribute(name, value);
    }

    public void view(String path) {
        // gugudan2.jsp 에게 나머지 작업을 토스
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/jsp/" + path + ".jsp");
        try {
            requestDispatcher.forward(req, resp);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath() {
        return req.getRequestURI();
    }

    public String getActionPath() {
        String[] bits = req.getRequestURI().split("/");

        return "/%s/%s/%s".formatted(bits[1], bits[2], bits[3]);
    }

    public String getRouteMethod() {
        String method = getParam("_method", "");

        if (method.length() > 0) {
            return method.toUpperCase();
        }

        return req.getMethod();
    }

    public long getLongPathValueByIndex(int index, long defaultValue) {
        String value = getPathValueByIndex(index, null);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public void replace(String uri, String msg) {
        if (msg != null && msg.trim().length() > 0) {
            println("""
                    <script>
                    alert("%s");
                    </script>
                    """.formatted(msg));
        }

        println("""
                <script>
                location.replace("%s");
                </script>
                """.formatted(uri));
    }

    public void historyBack(String msg) {
        if (msg != null && msg.trim().length() > 0) {
            println("""
                    <script>
                    alert("%s");
                    </script>
                    """.formatted(msg));
        }

        println("""
                <script>
                history.back();
                </script>
                """);
    }

    public void json(Object resultData) {
        resp.setContentType("application/json; charset=utf-8");

        String jsonStr = Ut.json.toStr(resultData, "");
        println(jsonStr);
    }

    public void json(Object data, String resultCode, String msg) {
        json(new ResultData(resultCode, msg, data));
    }

    public void successJson(Object data) {
        json(data, "S-1", "성공");
    }

    public void failJson(Object data) {
        json(data, "F-1", "실패");
    }

}