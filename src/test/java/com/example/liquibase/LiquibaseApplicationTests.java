package com.example.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = "spring.liquibase.drop-first=true")
record LiquibaseApplicationTests(JdbcTemplate template) {

    @Test
    void contextLoads() {
    }

}
