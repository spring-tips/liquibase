--liquibase formatted sql

--changeset jlong:1
create table articles
(
    id       serial primary key,
    title    varchar(255) not null,
    authored timestamp    not null
);

create table comments
(
    id         serial primary key,
    comment    varchar(255) not null,
    article_id bigint,
    constraint article_fk foreign key (article_id) references articles (id)
);
