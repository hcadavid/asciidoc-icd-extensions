FROM maven:3.8.4-jdk-11 as build
COPY ./ /asciidocext/
WORKDIR /asciidocext
RUN mvn compile
RUN chmod +x build_docs.sh
RUN mkdir /adocsrc
RUN mkdir /adocout
ENTRYPOINT ["./build_docs.sh", "/adocsrc","/adocout"]
