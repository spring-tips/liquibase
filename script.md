# Script

## getting started with Postgres
```shell
#!/usr/bin/env bash

NAME=${1}-postgres
PORT=${2:-5432}

docker run --name  $NAME  \
-p ${PORT}:5432 \
-e POSTGRES_USER=user \
-e PGUSER=user \
-e POSTGRES_PASSWORD=pw \
postgres:latest


./postgres.sh dev 5500
./postgres.sh prod 5400

PGPASSWORD=pw psql -U user -h localhost -p 5500 user
PGPASSWORD=pw psql -U user -h localhost -p 5400 user
```

## Install the basic schema running the sql in src/main/resources/bootstrap.sql

## Write a Simple Program to Use the SQL DB

```properties
spring.datasource.username=user
spring.datasource.password=pw
spring.datasource.url=jdbc:postgresql://localhost:5500/user
spring.liquibase.change-log=classpath:/db/changelog/changelog.sql
```


```java

 	@Bean
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

```

## add Liquibase to the Maven build

```xml
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>4.5.0</version>
    <configuration>
        <propertyFile>
            src/main/resources/liquibase.properties
        </propertyFile>
    </configuration>
</plugin>
```
## Simple Liquibase Property File src/main/resources/liquibase.properties
```properties
url=jdbc:postgresql://localhost:5400/user
username=user
password=pw
driver=org.postgresql.Driver
changeLogFile=src/main/resources/db/changelog/changelog.sql
outputChangeLogFile=src/main/resources/db/changelog/generated.sql
```

## Bootstrap Liquibase Changelog

`mvn liquibase:generateChangeLog`

Then move `generated.sql` to `changelog.sql`


## Reset DB and restart Spring Boot application, makign sure to specify location of changelog.sql in application.properties

notice that everythings there and were back where we started

## Reset DB and run `mvn liquibase:update`

## Reset DB and remove the last two changesets in .sql file then rerun liquibase:update

Notice the errors? We need to build some smarts into our migration so that it doesnt blow chunks when its probably ok

* introduce sql-preconditions for the first two steps
* then manually add the new column and the sample data and the pre-conditions
* then run liquibase:update



## Compute Delta

#!/usr/bin/env bash

#!/usr/bin/env bash

mvn liquibase:diff \
  -Dliquibase.diffChangeLogFile=diff.sql \
  -Dliquibase.referenceUrl=jdbc:postgresql://mbp2021.local:5500/user \
  -Dliquibase.referenceDriver=org.postgresql.Driver \
  -Dliquibase.referenceUsername=user \
  -Dliquibase.referencePassword=pw

