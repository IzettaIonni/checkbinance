FROM maven:3-openjdk-11 as builder
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn package

FROM openjdk:11-jdk
ENV DATASOURCE_URL=jdbc:postgresql://localhost:5432/CheckBinance
ENV DATASOURCE_USERNAME=postgres
ENV DATASOURCE_PASSWORD=123
COPY --from=builder /usr/app/target/checkbinance-0.0.1-SNAPSHOT.jar checkbinance-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT java -Dspring.datasource.url=$DATASOURCE_URL -Dspring.datasource.username=$DATASOURCE_USERNAME -Dspring.datasource.password=$DATASOURCE_PASSWORD -jar checkbinance-0.0.1-SNAPSHOT.jar 