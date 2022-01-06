
FROM atlassian/maven:3.5-jdk-8-alpine as build
USER root
RUN apk add --update nodejs nodejs-npm
RUN npm install -g bytefield-svg
RUN apk add graphviz
RUN mkdir /adocsrc
RUN mkdir /adocout
COPY ./ /asciidocext/
WORKDIR /asciidocext
RUN chown -R user /asciidocext
RUN chown -R user /adocsrc
RUN chown -R user /adocout
USER user
RUN mvn compile
RUN chmod +x build_docs.sh
# sudo npm install -g bytefield-svg
# required by PlantUML: sudo apt install graphviz
# network diagram pip3 install  nwdiag
#ENTRYPOINT ["./build_docs.sh", "/adocsrc","/adocout"]
