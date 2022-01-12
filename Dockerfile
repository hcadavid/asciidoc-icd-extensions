FROM maven:3.6.1-jdk-8-alpine as build
USER root

RUN addgroup -S appgroup && adduser -S user -G appgroup
#RUN apk add maven
RUN apk add ttf-dejavu

# NodeJS dependencies (to enable graphviz-based extensions)
#RUN apk add --update nodejs nodejs-npm
#RUN npm install -g bytefield-svg
RUN apk add graphviz
RUN apk add tree


# Python/PIP (to install nwdiag, sysrd2jinja)
ENV PYTHONUNBUFFERED=1
RUN apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python
RUN python3 -m ensurepip
RUN pip3 install --no-cache --upgrade pip setuptools


#RUN pip3 install nwdiag

# Source / Output folders 
RUN mkdir /adocsrc
RUN mkdir /adocout
COPY ./ /asciidocext/
WORKDIR /asciidocext

# Install sysrdl2jinja
#RUN sh sysrdl2jinja/install.sh

# Switch folder owners from root to user 
RUN chown -R user /asciidocext
RUN chown -R user /adocsrc
RUN chown -R user /adocout

# Build asciidoctor extensions
USER user
RUN mvn compile
RUN chmod +x build_docs.sh