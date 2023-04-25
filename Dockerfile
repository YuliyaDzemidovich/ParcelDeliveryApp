# Use the official Maven image to build the application
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory to /app
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn package -DskipTests

# Use the official Tomcat image with Temurin OpenJDK 17
FROM ubuntu:20.04 AS deploy

# Install JRE
RUN apt-get update && apt-get install -y openjdk-17-jre-headless

# Copy the WAR file to the tomcat webapps directory
COPY --from=build /app/target/ParcelDeliveryApp-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/parceldeliveryapp.war

# Download and extract Apache Tomcat to /opt/tomcat
RUN apt-get update && apt-get install -y wget \
    && mkdir -p /opt/tomcat \
    && wget -q -O /tmp/tomcat.tar.gz https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.8/bin/apache-tomcat-10.1.8.tar.gz \
    && tar xf /tmp/tomcat.tar.gz -C /opt/tomcat --strip-components=1 \
    && rm /tmp/tomcat.tar.gz

# Set environment variables
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENV APP_NAME="parceldeliveryapp"
ENV CATALINA_BASE=/opt/tomcat
ENV CATALINA_HOME=/opt/tomcat

# Copy the context.xml file into the container at /opt/tomcat/conf
COPY deployment/context.xml /opt/tomcat/conf/context.xml
COPY deployment/manager/context.xml /opt/tomcat/webapps/manager/META-INF/context.xml

# TODO delete manager role later
COPY deployment/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml

# Expose port 8080
EXPOSE 8080

# Add Tomcat bin directory to $PATH
ENV PATH="$PATH:/opt/tomcat/bin"
# Start Tomcat server
CMD ["catalina.sh", "run"]