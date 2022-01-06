
FROM atlassian/maven:3.5-jdk-8-alpine as build
USER root
RUN apk add --update nodejs nodejs-npm
RUN npm install -g bytefield-svg
RUN apk add graphviz

RUN cd sysrdl2jinja && setup.py install && cd ..
# Install python/pip
ENV PYTHONUNBUFFERED=1
RUN apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python
RUN python3 -m ensurepip
RUN pip3 install --no-cache --upgrade pip setuptools


#RUN pip3 install nwdiag
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
