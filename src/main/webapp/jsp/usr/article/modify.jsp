<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시글 수정</title>
</head>
<body>
<h1>게시글 수정</h1>
<form action="/usr/article/modify/${id}" method="POST">
    <input type="hidden" name="_method" value="PUT">
    <div>
        <label for="title">제목:</label>
        <input type="text" id="title" name="title">
    </div>
    <div>
        <label for="body">내용:</label>
        <textarea id="body" name="body"></textarea>
    </div>
    <button type="submit">수정</button>
</form>
</body>
</html>

