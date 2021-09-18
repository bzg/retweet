# Copyright (c) 2019-2021 Bastien Guerry <bzg@bzg.fr>
# SPDX-License-Identifier: EPL-2.0
# License-Filename: LICENSES/EPL-2.0.txt

FROM openjdk:8-alpine
ENV RETWEET_CONFIG ${RETWEET_CONFIG}
ADD target/retweet-0.3.6-standalone.jar /retweet-0.3.6-standalone.jar
CMD ["java", "-jar", "/retweet-0.3.6-standalone.jar"]
