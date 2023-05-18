<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>게시글 작성</title>
</head>
<body>
<h1>게시글 작성</h1>
<form action="/usr/article/write" method="post">
  <div>
    <label for="title">제목:</label>
    <input type="text" id="title" name="title">
  </div>
  <div>
    <label for="body">내용:</label>
    <textarea id="body" name="body"></textarea>
  </div>
  <button type="submit">작성</button>
</form>
</body>
</html>

