FROM maven:3.8.4-jdk-11 as build
COPY ./ /asciidocext/
WORKDIR /asciidocext
RUN mvn compile
