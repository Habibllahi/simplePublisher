FROM eclipse-temurin:17-jdk

LABEL org.opencontainers.image.title="simple-publisher" \
      org.opencontainers.image.description="Web REST server that demo rabbit MQ publisher" \
      org.opencontainers.image.authors="@javabeanstack"

RUN mkdir -p usr/service

COPY ./target/simplePublisher-0.0.1-SNAPSHOT.jar usr/service

WORKDIR usr/service

EXPOSE 8082

CMD ["java","-jar","./simplePublisher-0.0.1-SNAPSHOT.jar"]
