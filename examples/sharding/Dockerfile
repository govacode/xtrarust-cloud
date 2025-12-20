# docker build -t sharding:1.0 -f ./DockerFile .
# https://docs.spring.io/spring-boot/docs/3.2.12/reference/html/container-images.html#container-images.dockerfiles
# 第一阶段：构建应用
FROM eclipse-temurin:21.0.7_6-jdk-noble as builder
WORKDIR /builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
# 提取分层信息
RUN java -Djarmode=layertools -jar application.jar extract

# 第二阶段：运行应用
FROM eclipse-temurin:21.0.7_6-jdk-noble
WORKDIR /application
# 从构建阶段复制分层内容
COPY --from=builder /builder/dependencies/ ./
COPY --from=builder /builder/spring-boot-loader/ ./
COPY --from=builder /builder/snapshot-dependencies/ ./
COPY --from=builder /builder/application/ ./
# 设置默认 JVM 参数
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-XX:+UnlockDiagnosticVMOptions -XX:+UnlockExperimentalVMOptions -XX:-OmitStackTraceInFastThrow -XX:+AlwaysPreTouch -server -Xms2G -Xmx2G -XX:+UseZGC"
# 暴露端口
EXPOSE ${SERVER_PORT}
ENTRYPOINT exec java -Dserver.port=$SERVER_PORT $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher