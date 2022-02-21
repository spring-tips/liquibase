--liquibase formatted sql
--changeset jlong:2
alter table articles
    add column published timestamp null;
--rollback alter table articles drop column published;
