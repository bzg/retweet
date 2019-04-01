# Copyright (c) 2019 Bastien Guerry <bzg@bzg.fr>

# SPDX-License-Identifier: EPL-2.0
# License-Filename: LICENSES/EPL-2.0.txt

FROM openjdk:8-alpine

ENV CONSUMER_KEY ${CONSUMER_KEY}
ENV CONSUMER_SECRET ${CONSUMER_SECRET}
ENV ACCESS_TOKEN ${ACCESS_TOKEN}
ENV ACCESS_TOKEN_SECRET ${ACCESS_TOKEN_SECRET}

ADD target/retweet-0.3-standalone.jar /retweet-0.3-standalone.jar
ADD config.edn config.edn

CMD ["java", "-jar", "/retweet-0.3-standalone.jar"]
