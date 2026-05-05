FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml ./pom.xml
COPY eureka-server/pom.xml ./eureka-server/pom.xml
COPY config-server/pom.xml ./config-server/pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
COPY admin-service/pom.xml ./admin-service/pom.xml
COPY product-service/pom.xml ./product-service/pom.xml
COPY cart-service/pom.xml ./cart-service/pom.xml
COPY wishlist-service/pom.xml ./wishlist-service/pom.xml
COPY customer-details-service/pom.xml ./customer-details-service/pom.xml
COPY order-service/pom.xml ./order-service/pom.xml
COPY feedback-service/pom.xml ./feedback-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY admin-service/src ./admin-service/src
RUN mvn -N install -q
RUN mvn -pl admin-service -Dmaven.test.skip=true package -q
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/admin-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]