# BUILD STAGE

FROM maven:3.9.11-eclipse-temurin-17-alpine AS build

WORKDIR /airlink

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests



#RUNTIME STAGE

FROM eclipse-temurin:17-jre-alpine-3.20

LABEL maintiner="Karunamaymurmu@gmail.com"
LABEL version=0.0.1
LABEL descripion="Airlink"

RUN addgroup -S appuser && adduser -S -G appuser airlink

WORKDIR /airlink

COPY --from=build /airlink/target/*.jar app.jar

RUN chown -R airlink:appuser /airlink

USER airlink

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+HeapDumpOnOutOfMemoryError \
               -XX:HeapDumpPath=/tmp/dump.hprof \
               -Xlog:gc*,gc+heap=info:file=/tmp/gc.log:time,uptime,level,tags"


ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]