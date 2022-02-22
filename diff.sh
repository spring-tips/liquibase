#!/usr/bin/env bash

mvn liquibase:diff \
 -Dliquibase.diffChangeLogFile=diff.sql \
 -Dliquibase.referenceUrl=jdbc:postgresql://localhost:5500/user \
 -Dliquibase.referenceUsername=user \
 -Dliquibase.referencePassword=pw \
 -Dliquibase.referenceDriver=org.postgresql.Driver



