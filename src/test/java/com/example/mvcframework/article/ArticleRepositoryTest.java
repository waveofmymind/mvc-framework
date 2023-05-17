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
    public void task() {
        myMap = Container.getObj(MyMap.class);
        this.articleRepository = Container.getObj(ArticleRepository.class);
    }

    @BeforeEach
    public void beforeEach() {
        // 게시물 테이블을 깔끔하게 삭제한다.
        // DELETE FROM article; // 보다 TRUNCATE article; 로 삭제하는게 더 깔끔하고 흔적이 남지 않는다.
        truncateArticleTable();
        // 게시물 3개를 만든다.
        // 테스트에 필요한 샘플데이터를 만든다고 보면 된다.
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
        Assertions.assertThat(articleId).isEqualTo(1L);
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



}