FROM eclipse-temurin:17-jre
COPY server/target/WallpaperVideo-jar-with-dependencies.jar /usr/app/WallpaperVideo-jar-with-dependencies.jar
WORKDIR /usr/app
VOLUME /video
ENV PORT="9877"
ENV VideoCache="FALSE"
ENV FILE="/video"
ENV TZ="Asia/Shanghai"
EXPOSE 9877
RUN ln -s /usr/java/openjdk-17 /opt/java/openjdk
CMD ["java", "-jar", "WallpaperVideo-jar-with-dependencies.jar"]
