FROM openjdk:8-jdk-alpine

ARG JAR_FILE
#ARG JAVA_OPTS

RUN apk add --no-cache bash gawk sed grep bc coreutils busybox-extras tzdata

RUN adduser -D -g 'ay' ay

RUN mkdir /apps
COPY ./target/${JAR_FILE} /apps/app.jar
COPY ./entrypoint.sh /apps/entrypoint.sh

RUN chmod +x /apps/entrypoint.sh
CMD ["/apps/entrypoint.sh"]