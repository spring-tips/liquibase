package com.example.liquibase;

import lombok.SneakyThrows;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class LiquibaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiquibaseApplication.class, args);
    }

    @Bean
    @DependsOnDatabaseInitialization
    ApplicationRunner runner(JdbcTemplate template) {
        return args -> {
            var sql = """
                      select 
                        a.id as aid, 
                        a.authored as authored ,  
                        a.title as title, 
                        c.comment, 
                        c.id as cid    
                        from articles a  
                        left join comments c 
                        on a.id = c.article_id  
                    """;
            new HashSet<>(template.query(sql, new ArticleCommentRowMapper()))
                    .forEach(System.out::println);

        };
    }
}


class ArticleCommentRowMapper implements RowMapper<Article> {

    private final Map<Long, Article> articles = new ConcurrentHashMap<>();

    @SneakyThrows
    private Article build(Long aid, ResultSet rs) {
        return new Article(aid, rs.getString("title"), rs.getDate("authored"), new ArrayList<>());
    }

    @Override
    public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
        var articleId = rs.getLong("aid");
        var article = this.articles.computeIfAbsent(articleId, aid -> build(aid, rs));
        var commentId = rs.getLong("cid");
        if (commentId > 0) {
            article.comments().add(new Comment(commentId, rs.getString("comment")));
        }
        return article;
    }
}


record Article(Long id, String title, Date authored, List<Comment> comments) {
}

record Comment(Long id, String text) {
}