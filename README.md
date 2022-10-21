### requirements:

+ jdk 11 (or higher)
+ maven 3.8.6 (or higher)

Three profiles and append a -P parameter to switch which Maven profile will be applied:

* dev (default)
* test
* prod

How to run a local app, run following commands:

* mvn clean compile -U
* mvn flyway:migrate
* mvn spring-boot:run

How to (target folder):

* build - mvn clean package
* run local - java -jar <jarname>.jar

with profile

* build - mvn clean package -Pprod
* run local - java -jar <jarname>.jar

How to set db settings for profiles (dev, test, prod) in pom.xml

```xml

<properties>
    <spring.migration.datasource.schema>postgres</spring.migration.datasource.schema>
    <spring.migration.datasource.url>jdbc:postgresql://localhost:5432/${spring.migration.datasource.schema}
    </spring.migration.datasource.url>
    <spring.migration.datasource.username>postgres</spring.migration.datasource.username>
    <spring.migration.datasource.password>postgres</spring.migration.datasource.password>
    <spring.client.datasource.url>jdbc:postgresql://dev.lacerta.by/stagelacerta</spring.client.datasource.url>
    <spring.client.datasource.username>postgres</spring.client.datasource.username>
    <spring.client.datasource.password>spartakmoscow1922</spring.client.datasource.password>
    <chat.base.url>https://dev02.nestegg.by:8008</chat.base.url>
    <chat.user.domain>dev02.nestegg.by</chat.user.domain>
    <chat.type>m.login.password</chat.type>
    <chat.user>root</chat.user>
    <chat.password>aZohje5taj</chat.password>
    <client.server.url>https://localhost:8080</client.server.url>
</properties>
```

it needs for (application.properties)

```clojure
spring.migration.datasource.url=@spring.migration.datasource.url@
spring.migration.datasource.username=@spring.migration.datasource.username@
spring.migration.datasource.password=@spring.migration.datasource.password@
spring.migration.datasource.driverClassName=org.postgresql.Driver
spring.client.datasource.url=@spring.client.datasource.url@
spring.client.datasource.username=@spring.client.datasource.username@
spring.client.datasource.password=@spring.client.datasource.password@
chat.base.url=@chat.base.url@
chat.user.domain=@chat.user.domain@
chat.type=@chat.type@
chat.user=@chat.user@
chat.password=@chat.password@
client.server.url=@client.server.url@
```

Api Documentation (Swagger)

* http://localhost:8080/swagger-ui/index.html (local host)
* http://host/context-path/swagger-ui/index.html

Install and run SonarQube

* docker pull sonarqube:latest
* docker container run -d -p 9000:9000 --name sonarserver sonarqube:latest

SonarQube verify

* mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
* mvn clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin

Spotbugs verify

* mvn com.github.spotbugs:spotbugs-maven-plugin:spotbugs
