package com.example.mvcframework.article;

import com.example.mvcframework.db.MyMap;
import com.example.mvcframework.spring.Container;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArticleServiceTest {

    private MyMap myMap;
    private ArticleService articleService;
    private static final int TEST_DATA_SIZE = 100;

    public ArticleServiceTest() {
        myMap = Container.getObj(MyMap.class);
        articleService = Container.getObj(ArticleService.class);
    }

    @BeforeAll
    public void BeforeAll() {
        myMap.setDevMode(true);
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
        assertThat(articleService).isNotNull();
    }

    @DisplayName("게시글을 조회했을 때, 모든 게시글이 조회된다.")
    @Test
    public void getArticles() {
        List<Article> articleDtoList = articleService.getArticles();
        assertThat(articleDtoList.size()).isEqualTo(TEST_DATA_SIZE);
    }

    @DisplayName("id 값으로 데이터를 조회했을때 특정 게시글이 조회된다.")
    @Test
    public void getArticleById() {
        Article articleDto = articleService.getArticleById(1);

        assertThat(articleDto.getId()).isEqualTo(1L);
        assertThat(articleDto.getTitle()).isEqualTo("제목1");
        assertThat(articleDto.getBody()).isEqualTo("내용1");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isFalse();
    }

    @DisplayName("게시글의 총 개수를 조회한다.")
    @Test
    public void getArticlesCount() {
        long articlesCount = articleService.getArticlesCount();

        assertThat(articlesCount).isEqualTo(TEST_DATA_SIZE);
    }

    @DisplayName("제목, 내용, 비공개 유무로 게시글을 작성할 수 있다.")
    @Test
    public void write() {
        long newArticleId = articleService.write("제목 new", "내용 new", false);

        Article articleDto = articleService.getArticleById(newArticleId);

        assertThat(articleDto.getId()).isEqualTo(newArticleId);
        assertThat(articleDto.getTitle()).isEqualTo("제목 new");
        assertThat(articleDto.getBody()).isEqualTo("내용 new");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isEqualTo(false);
    }

    @DisplayName("id,제목, 내용, 비공개 유무로 게시글을 수정할 수 있다.")
    @Test
    public void modify() {
        //Ut.sleep(5000);

        articleService.modify(1, "제목 new", "내용 new", true);

        Article articleDto = articleService.getArticleById(1);

        assertThat(articleDto.getId()).isEqualTo(1);
        assertThat(articleDto.getTitle()).isEqualTo("제목 new");
        assertThat(articleDto.getBody()).isEqualTo("내용 new");
        assertThat(articleDto.isBlind()).isEqualTo(true);

        // DB에서 받아온 게시물 수정날짜와 자바에서 계산한 현재 날짜를 비교하여(초단위)
        // 그것이 1초 이하로 차이가 난다면
        // 수정날짜가 갱신되었다 라고 볼 수 있음
        long diffSeconds = ChronoUnit.SECONDS.between(articleDto.getModifiedDate(), LocalDateTime.now());
        assertThat(diffSeconds).isLessThanOrEqualTo(1L);
    }

    @DisplayName("id로 게시글을 삭제할 수 있다.")
    @Test
    public void delete() {
        articleService.delete(1);

        Article articleDto = articleService.getArticleById(1);

        assertThat(articleDto).isNull();
    }

    @Test
    public void _2번글의_이전글은_1번글_이다() {
        Article id2Article = articleService.getArticleById(2);
        Article id1Article = articleService.getPrevArticle(id2Article);

        assertThat(id1Article.getId()).isEqualTo(1);
    }

    @Test
    public void _1번글의_이전글은_없다() {
        Article id1Article = articleService.getArticleById(1);
        Article nullArticle = articleService.getPrevArticle(id1Article);

        assertThat(nullArticle).isNull();
    }

    @Test
    public void _2번글의_다음글은_3번글_이다() {
        Article id3Article = articleService.getNextArticle(2);

        assertThat(id3Article.getId()).isEqualTo(3);
    }

    @Test
    public void 마지막글의_다음글은_없다() {
        long lastArticleId = TEST_DATA_SIZE;
        Article nullArticle = articleService.getNextArticle(lastArticleId);

        assertThat(nullArticle).isNull();
    }

    @Test
    public void _10번글의_다음글은_21번글_이다_왜냐하면_11번글부터_20번글까지는_블라인드라서() {
        Article nextArticle = articleService.getNextArticle(10);

        assertThat(nextArticle.getId()).isEqualTo(21);
    }
}


