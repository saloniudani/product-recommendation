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

### Notes about embedded Solr setup
For Embedded Solr server, following config files are mandatory . Download actual Apache Solr artifact and use config files from it. Note that Aache Solr version should be same as that used by spring-data-solr.
For this app Solr version is 8.5.2 and SOLR_HOME is solr/embedded.

1 Copy solr-8.5.2/server/solr/solr.xml to your SOLR_HOME directory.
```
SOLR_HOME/solr.xml
```
2 Create following folder structure inside SOLR_HOME for each of your collections. For this app it is:
```
SOLR_HOME/product
SOLR_HOME/productpurchase
```
3 Create core.properties file for each of your collection
```
SOLR_HOME/product/core.properties
SOLR_HOME/productpurchase/core.properties
```
4 Content of core.properties should have your collection name. e.g for product collection it looks like.
```
name=product
```
5 Copy solr-8.5.2/server/solr/configsets/_default/conf inside each of your collection folders.
```
SOLR_HOME/product/conf
SOLR_HOME/productpurchase/conf
```
6 Note that when EmbeddedSolrServer is created 
**com/recommendation/purchasedemo/config/AppSolrConfig.java:19**  ,
first argument is SOLR_HOME and second is any one collection name.