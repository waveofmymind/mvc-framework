package com.example.mvcframework.spring;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usr/*")
public class DispatchServlet extends HttpServlet {
    // 조회
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String httpMethod = determineHttpMethod(req);
        ControllerManager.runAction(httpMethod, req, resp);
    }

    // 등록
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String httpMethod = determineHttpMethod(req);
        ControllerManager.runAction(httpMethod, req, resp);
    }

    // 삭제
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String httpMethod = determineHttpMethod(req);
        ControllerManager.runAction(httpMethod, req, resp);
    }

    // 수정
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String httpMethod = determineHttpMethod(req);
        ControllerManager.runAction(httpMethod, req, resp);
    }

    private String determineHttpMethod(HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String hiddenMethod = req.getParameter("_method");
        if (hiddenMethod != null && !hiddenMethod.isEmpty()) {
            requestMethod = hiddenMethod.toUpperCase();
        }

        if (req.getRequestURI().matches("/usr/article/delete/\\d+")) {
            requestMethod = "DELETE";
        }

        return requestMethod;
    }
}
