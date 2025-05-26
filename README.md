# Goods storage
### Описание
Данный проект создан в учебных целях (для изучения разработки backend приложений).

Приложение хранит список товаров и список клиентов.
Каждый клиент может заказать необходимые ему товар и получить их после подтверждения.

### Технологии
* Java
* Spring Boot
* JPA
* Hibernate
* SQL (PostgreSQL)
* Maven
* Docker
* Mockito, JUnit
* Kafka
* Minio

### Запуск проекта
**<span style="color:orange">Внимание!</span>** Для запуска проекта необходимы git и docker
* Склонируйте репозиторий: git clone https://github.com/Constantin846/java-goods-storage.git
* Перейдите в директорию проекта: cd java-goods-storage
* Создайте исполняемый jar: mvn package
* Создайте и запустите docker контейнеры:
    docker-compose -f docker-compose.yml -f docker-compose-minio.yml up


**<span style="color:orange">Внимание!</span>** Возможен запуск docker контейнеров по одному. 
Запуск приложения раньше файлового хранилища приведет 
к потере функциональности сохранения и получения файлов. 

### API
На данный момент ознакомиться с API можно после запуска приложения по ссылке http://localhost:8080/gs/swagger-ui/index.html



