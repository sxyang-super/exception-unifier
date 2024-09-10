@echo off

REM Set MAVEN_OPTS for debugging
set MAVEN_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000

REM Execute mvnw with the provided arguments
call mvnw %*

REM Clean up MAVEN_OPTS after execution to avoid leaving debug options set
set MAVEN_OPTS=
