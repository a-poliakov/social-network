FROM openjdk:8-jdk-alpine

ARG JAR_FILE
#ARG JAVA_OPTS

RUN mkdir /apps
COPY ./target/${JAR_FILE} /apps/app.jar
COPY ./entrypoint.sh /apps/entrypoint.sh

RUN chmod +x /apps/entrypoint.sh
CMD ["/apps/entrypoint.sh"]