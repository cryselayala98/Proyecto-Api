# Proyecto Api

[Docker Hub](https://hub.docker.com/repository/docker/criselayala98/api/general)

Para arrancar la aplicación:     


`docker-compose up -d`


### Para probar la aplicación en modo desarrollo:

1. montar las imágenes de la base de datos y de la caché

   `docker-compose up -d db`

   `docker-compose up -d redis`
    
2. Ejecutar el siguiente comando:

   `mvn spring-boot:run`

### Para acceder a la documentación de los endpoints

[Ver en ApiHub](https://app.swaggerhub.com/apis/Criselayala/ApiDoc/1.0#/servers)


`http://localhost:8080/api/webjars/swagger-ui/index.html`



    