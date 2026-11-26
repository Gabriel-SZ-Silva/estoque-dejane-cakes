# Monta o SistemaConfeitaria dentro de um Tomcat 11, sem precisar de NetBeans nem de Ant.

# Etapa 1: compila o código Java e monta a pasta da aplicação.
FROM tomcat:11.0-jdk25-temurin-noble AS build
WORKDIR /app
COPY src/java ./src/java
COPY web ./web
# O driver do MySQL já vem no projeto, em web/WEB-INF/lib, e é copiado junto com as telas.
RUN mkdir -p ROOT/WEB-INF/classes \
 && cp -r web/. ROOT/ \
 && javac -encoding UTF-8 -d ROOT/WEB-INF/classes -cp "$CATALINA_HOME/lib/*" $(find src/java -name '*.java')

# Etapa 2: só o Tomcat e a aplicação pronta, como página principal do endereço.
FROM tomcat:11.0-jre25-temurin-noble
RUN rm -rf "$CATALINA_HOME"/webapps/*
COPY --from=build /app/ROOT "$CATALINA_HOME/webapps/ROOT"
EXPOSE 8080
