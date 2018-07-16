FROM gradle:jdk8 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:8-jre-slim
EXPOSE 8080
COPY --from=builder /home/gradle/src/build/distributions/jble6lowpanshoveld.tar /app/
WORKDIR /app
RUN tar -xvf jble6lowpanshoveld.tar
WORKDIR /app/jble6lowpanshoveld
CMD bin/jble6lowpanshoveld -configFile /app/jble6lowpanshoveld/jble6lowpanshoveld.conf