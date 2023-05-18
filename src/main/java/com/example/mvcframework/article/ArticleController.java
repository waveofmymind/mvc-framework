package com.example.mvcframework.article;

import com.example.mvcframework.db.Rq;
import com.example.mvcframework.spring.annotation.Autowired;
import com.example.mvcframework.spring.annotation.Controller;
import com.example.mvcframework.spring.annotation.mapping.GetMapping;
import com.example.mvcframework.spring.annotation.mapping.PostMapping;

import java.util.List;

// ArticleController 가 컨트롤러 이다.
// 아래 ArticleController 클래스는 Controller 이다.
@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/usr/article/list")
    public void showList(Rq rq) {
        List<Article> articles = articleService.getArticles();
        System.out.println("되나요");
        rq.setAttr("articles", articles);
        rq.view("usr/article/list"); // src/main/webapp/jsp/usr/article/list.jsp 실행
    }

    @GetMapping("/usr/article/write")
    public void showWrite(Rq rq) {
        rq.view("usr/article/write");
    }

    @PostMapping("/usr/article/write")
    public void write(Rq rq) {
        String title = rq.getParam("title", "");
        String body = rq.getParam("body", "");

        if (title.length() == 0) {

            rq.historyBack("제목을 입력해주세요.");
            return;
        }

        if (body.length() == 0) {
            rq.historyBack("내용을 입력해주세요.");
            return;
        }

        long id = articleService.write(title, body);

        rq.replace("/usr/article/%d".formatted(id), "%d번 게시물이 생성 되었습니다.".formatted(id));
    }


    @GetMapping("/usr/article/{id}")
    public void showDetail(Rq rq) {

        //TODO: @PathVariable 구현하기 또는 {id} 값 동적으로 받
        long id = Long.parseLong(rq.getPathParam("id", "2"));
        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        Article article = articleService.getArticleById(id);

        if (article == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        Article prevArticle = articleService.getPrevArticle(article.getId());
        Article nextArticle = articleService.getNextArticle(article.getId());

        rq.setAttr("prevArticle", prevArticle);
        rq.setAttr("nextArticle", nextArticle);

        List<Article> articles = articleService.getArticles();

        rq.setAttr("articles", articles);

        rq.setAttr("article", article);
        rq.view("usr/article/detail");
    }


}
