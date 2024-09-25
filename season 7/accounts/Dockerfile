#Start with base image containing Java runtime
FROM openjdk:17-jdk-slim

#Information around who mantains the image
MAINTAINER ias.com

#Add the application's jar to the image
COPY build/libs/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

#Execute the application
ENTRYPOINT ["java", "-jar", "accounts-0.0.1-SNAPSHOT.jar"]