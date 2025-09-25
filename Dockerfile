FROM openjdk:21
EXPOSE 8080
ARG JAR_FILE
ADD target/${JAR_FILE} /final-waiter-service.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Taipei", "-jar", "/final-waiter-service.jar"]