FROM gradle:7.6.1-jdk17 AS compile-image

WORKDIR /app
ARG GRADLE_ARG="build -x check -x test"
COPY src /app/src
COPY *.kts /app/
USER root
RUN chown -R gradle /app
USER gradle
RUN gradle ${GRADLE_ARG}

FROM openjdk:17-jdk AS runtime-image

EXPOSE 8080
CMD [ "/bin/bash", "-c", "java -jar /app.jar"]
COPY --from=compile-image /app/build/libs/postgresql-insertion.jar /app.jar
