#USER-SERVICE

##Run Application using Docker

1. Run `mvn clean package` it will build appropriate Jar file
2. Run `docker build -t user-service-image .` to build image based to Dockerfile and add tag **_user-service-image_** 
3. Run `docker run -p 8080:8080 user-service-image` to map ports
