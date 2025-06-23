# Usar una imagen base de OpenJDK con la versión de Java que usas (Java 21)
FROM openjdk:21-jdk-slim

# Etiqueta para mantener el JAR de tu aplicación (se construye en target/)
ARG JAR_FILE=target/*.jar

# Copiar el JAR de tu aplicación al contenedor
COPY ${JAR_FILE} app.jar

# Exponer el puerto en el que tu aplicación Spring Boot escucha (por defecto 8080)
EXPOSE 8090

# Comando para ejecutar la aplicación Spring Boot cuando el contenedor se inicie
ENTRYPOINT ["java", "-jar", "app.jar"]