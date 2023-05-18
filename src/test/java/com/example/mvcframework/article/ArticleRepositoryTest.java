package com.example.mvcframework.article;

import com.example.mvcframework.db.MyMap;
import com.example.mvcframework.spring.Container;
import com.example.mvcframework.spring.annotation.Autowired;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArticleRepositoryTest {

    private ArticleRepository articleRepository;

    private static final int TEST_DATA_SIZE = 100;

    private MyMap myMap;

    @BeforeAll
    public void beforeAll() {
        myMap = Container.getObj(MyMap.class);
        this.articleRepository = Container.getObj(ArticleRepository.class);
    }

    @BeforeEach
    public void beforeEach() {
        truncateArticleTable();
        makeArticleTestData();
    }

    private void makeArticleTestData() {
        IntStream.rangeClosed(1, TEST_DATA_SIZE).forEach(no -> {
            boolean isBlind = no >= 11 && no <= 20;
            String title = "제목%d".formatted(no);
            String body = "내용%d".formatted(no);

            myMap.run("""
                    INSERT INTO article
                    SET createdDate = NOW(),
                    modifiedDate = NOW(),
                    title = ?,
                    `body` = ?,
                    isBlind = ?
                    """, title, body, isBlind);
        });
    }

    private void truncateArticleTable() {
        // 테이블을 깔끔하게 지워준다.
        myMap.run("TRUNCATE article");
    }


    @Test
    public void 존재한다() {
        Assertions.assertThat(articleRepository).isNotNull();
    }

    @DisplayName("게시글 작성시 성공한다.")
    @Test
    void write() {
        //given
        Article article = new Article(1L, LocalDateTime.now(),LocalDateTime.now(),"제목", "내용", false);
        //when
        long articleId = articleRepository.write(article.getTitle(), article.getBody(), article.isBlind());
        //then
        Assertions.assertThat(articleId).isEqualTo(101L);
    }

    @DisplayName("id로 게시글 조회시 성공한다.")
    @Test
    void getArticleById() {
        //given
        Long articleId = 1L;
        //when
        Article findArticle = articleRepository.getArticleById(articleId);
        //then
        Assertions.assertThat(findArticle.getId()).isEqualTo(articleId);
    }

    @DisplayName("올바른 id로 게시글을 수정시 성공한다.")
    @Test
    void modify() {
        //given
        Long articleId = 1L;
        //when
        articleRepository.modify(articleId, "수정된 제목", "수정된 내용", true);
        //then
        Article findArticle = articleRepository.getArticleById(articleId);
        Assertions.assertThat(findArticle.getTitle()).isEqualTo("수정된 제목");
        Assertions.assertThat(findArticle.getBody()).isEqualTo("수정된 내용");
        Assertions.assertThat(findArticle.isBlind()).isEqualTo(true);
    }

    @DisplayName("올바르지 않은 id로 수정을 요청하면 실패한다.")
    @Test
    void modify2() {
        //given
        Long articleId = 200L;
        //when
        articleRepository.modify(articleId, "수정된 제목", "수정된 내용", true);
        //then
        Article findArticle = articleRepository.getArticleById(articleId);
        Assertions.assertThat(findArticle).isNull();
    }






}