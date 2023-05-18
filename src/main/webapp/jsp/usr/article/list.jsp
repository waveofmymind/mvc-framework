<%@ page import="com.example.mvcframework.article.Article" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시글 목록</title>
</head>
<body>
<h1>게시글 목록</h1>
<%
    List<Article> articles = (List<Article>) request.getAttribute("articles");
    for (Article article : articles) {
%>
<div>
    <h2><a href="/usr/article/<%= article.getId() %>"><%= article.getTitle() %></a></h2>
    <p><%= article.getBody() %></p>
    <p><%= article.getCreatedDate() %></p>
</div>
<% } %>
</body>
</html>
