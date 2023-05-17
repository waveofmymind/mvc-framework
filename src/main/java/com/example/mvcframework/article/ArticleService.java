package com.example.mvcframework.article;


import com.example.mvcframework.spring.annotation.Autowired;
import com.example.mvcframework.spring.annotation.Service;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getArticles() {
        return articleRepository.getArticles();
    }

    public Article getArticleById(long id) {
        return articleRepository.getArticleById(id);
    }

    public long getArticlesCount() {
        return articleRepository.getArticlesCount();
    }

    public long write(String title, String body) {
        return write(title, body, false);
    }

    public long write(String title, String body, boolean isBlind) {
        return articleRepository.write(title, body, isBlind);
    }

    public void modify(long id, String title, String body) {
        modify(id, title, body, false);
    }

    public void modify(long id, String title, String body, boolean isBlind) {
        articleRepository.modify(id, title, body, isBlind);
    }

    public void delete(long id) {
        articleRepository.delete(id);
    }

    public Article getPrevArticle(Article article) {
        return getPrevArticle(article.getId());
    }

    public Article getPrevArticle(long id) {
        return articleRepository.getPrevArticle(id);
    }

    public Article getNextArticle(long id) {
        return articleRepository.getNextArticle(id);
    }
}