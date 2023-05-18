<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Article Detail</title>
</head>
<body>
<h1>${article.title}</h1>
<p>${article.body}</p>

<c:if test="${prevArticle != null}">
    <a href="/usr/article/${prevArticle.id}">Previous Article</a>
</c:if>

<c:if test="${nextArticle != null}">
    <a href="/usr/article/${nextArticle.id}">Next Article</a>
</c:if>
</body>
</html>
