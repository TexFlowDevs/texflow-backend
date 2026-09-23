# Build em duas etapas: a primeira compila com Maven (imagem grande, com
# Maven + JDK), a segunda so' carrega o .jar ja pronto numa imagem bem mais
# leve (so' o Java) - assim o Render nao precisa ter Maven instalado, o
# Dockerfile ja trata tudo sozinho, do jeito que ja roda localmente.

# Etapa 1: compila o projeto e gera o .jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: imagem final que o Render realmente roda
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# O Render injeta a variavel PORT com a porta certa (server.port=${PORT:8080}
# no application.properties ja le ela) - o EXPOSE aqui e' so' documentacao.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
