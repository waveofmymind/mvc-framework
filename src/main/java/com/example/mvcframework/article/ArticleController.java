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

    @GetMapping("/usr/article/modify/{id}")
    public void showModify(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        Article article = articleService.getArticleById(id);

        if (article == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        String articleBody = article.getBody();
        // 토스트UI 에디터 특성상 script를 t-script 로 치환해야 함
        // 나중에 처리
        rq.setAttr("id", id);
        rq.setAttr("articleBody", articleBody);
        rq.setAttr("article", article);
        rq.view("usr/article/modify");
    }

    @PostMapping("/usr/article/modify/{id}")
    public void modify(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        Article article = articleService.getArticleById(id);

        if (article == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

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

        articleService.modify(id, title, body);

        rq.replace("/usr/article/%d".formatted(id), "%d번 게시물이 수정 되었습니다.".formatted(id));
    }

    @GetMapping("/usr/article/delete/{id}")
    public void delete(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        Article article = articleService.getArticleById(id);

        if (article == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        articleService.delete(id);

        rq.replace("/usr/article/list", "%d번 게시물이 삭제 되었습니다.".formatted(id));
    }



}
