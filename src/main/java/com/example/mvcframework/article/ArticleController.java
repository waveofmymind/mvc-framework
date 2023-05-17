package com.example.mvcframework.article;

import com.example.mvcframework.db.Rq;
import com.example.mvcframework.spring.annotation.Autowired;
import com.example.mvcframework.spring.annotation.Controller;
import com.example.mvcframework.spring.annotation.GetMapping;

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


}
