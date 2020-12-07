# product-recommendation
A sample product recomendation springboot app with embedded Solr server.

### Pre- requisite
JAVA_HOME pointing to java 11

### Setup
1 Clone this repository
```
git clone https://github.com/saloniudani/product-recommendation.git
```
2 Switch to project directory
```
cd product-recommendation/
```
3 Build project
```
./mvnw clean install
```
4 Run SpringBoot App
```
./mvnw spring-boot:run
```

### Test using Browser or Postman
1 Hit the following URL with input product id <PRODUCT_ID>. Response will be top 5 products to be recommended alongside the given one.
```
http://localhost:8080/core-app-api/recommendation/product/<PRODUCT_ID>

e.g
http://localhost:8080/core-app-api/recommendation/product/id_261
```

### Consecutive App runs
1 Unless content of data files (src/main/resources/staged-data) change, kindly comment out following property in src/main/resources/application.properties to avoid ingestion of data on every run.
```
#app.mock.data.path=classpath*:staged-data/*.json;
```

### Note
