package com.example.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Date;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.liquibase.drop-first=true")
record LiquibaseApplicationTests(JdbcTemplate template, ArticleService articleService) {

    @Autowired
    LiquibaseApplicationTests {
    }

    @Test
    void contextLoads() {
        create(articleService, new Date(), "Beat the Queue with this simple trick: Apache Kafka");
        create(articleService, new Date(), "Waiter! There's a bug in my JSoup!", "this made me laugh and cry", "you  too will believe a man can try", "I love beautiful soup in Python and I love JSoup in Java");
        create(articleService, new Date(), "You Can Get to Production with These Ten Easy Tricks", "liar! There are only two tricks!");
        articleService.findAll().forEach(a -> out.println(a.toString()));
        assertTrue(articleService.findAll().size() == 3, "there should be 3 articles in the DB");
    }

    private static void create(ArticleService articleService, Date authored, String title, String... comments) {
        var article = articleService.createDraft(title, authored);
        Arrays.asList(comments).forEach(c -> articleService.addComment(article, c));
    }
}
