# acceleration-insertion-postgresql-joker2023

Description:
This is tested application for testing different approaches insertion data to PostgresSQL
The application has a backend which written on kotlin and a prepared postgres DB with 100_000_000 rows in a general table "payment_document".
The table "payment_document" has several index to do affect on insert data.

Prerequisite:
1. For work with prepared DB your computer should have at last 32 Gb free space. Because this one included 100_000_000 rows table "payment_document".

Work with prepared DB:
1. Download prepared image postgres with data >>> [db_joker.tar.gz](https://disk.yandex.ru/d/lzqUyby5aIFadw)
2. Load this image to your local registry `sudo docker load < db_joker.tar.gz`
3. Start application with git bash `sh start.sh`
4. Stop application with git bash `sh stop.sh`

Work with empty DB:
1. Change docker-compose.yaml from `image: db_joker:1` to `image: postgres:latest`
2. Start application `sudo docker-compose up -d --build`
3. Stop application with git bash `sudo docker-compose down -v`

The application has several endpoints for testing different approaches to insert data. 
All of them you can set count of data to generate row in DB and get result with measurements.
I advise call these endpoints by curl.

Endpoints:
- curl -d "" http://localhost:8080/test-insertion/spring/{count} - create data by spring.
- curl -d "" http://localhost:8080/test-insertion/spring-with-copy/{count} - create data by spring and copy method with saving file to disk.
- curl -d "" http://localhost:8080/test-insertion/spring-update/{count} - update data by spring. Data will be saved on 100_000 rows.
- curl -d "" http://localhost:8080/test-insertion/insert/{count} - create data by insert method. Data will be saved on 100_000 rows.
- curl -d "" http://localhost:8080/test-insertion/insert-with-transaction/{count} - create data by insert method. All data will be saved in one transaction.
- curl -d "" http://localhost:8080/test-insertion/insert-by-property-with-transaction/{count} - create data by KProperty map insert method. All data will be saved in one transaction.
- curl -d "" http://localhost:8080/test-insertion/insert-with-drop-index/{count} - create data by insert method with drop index before transaction and recreating this one after that.
- curl -d "" http://localhost:8080/test-insertion/copy/{count} - create data by copy method without saving file to disk. Data will be saved on 100_000 rows.
- curl -d "" http://localhost:8080/test-insertion/copy-with-transaction/{count} - create data by copy method without saving file to disk. All data will be saved in one transaction.
- curl -d "" http://localhost:8080/test-insertion/copy-by-property-with-transaction/{count} - create data with KProperty map by copy method without saving file to disk. All data will be saved in one transaction.
- curl -d "" http://localhost:8080/test-insertion/copy-by-binary-with-transaction/{count} - create data by copy method with binary transformation. All data will be saved in one transaction.
- curl -d "" http://localhost:8080/test-insertion/copy-by-binary-and-property-with-transaction/{count} - create data with KProperty map by copy method with binary transformation. All data will be saved in one transaction.
- curl -d "" http://localhost:8080/test-insertion/copy-by-file/{count} - create data by copy method with saving file to disk.
- curl -d "" http://localhost:8080/test-insertion/copy-by-binary-file/{count} - create data by copy method with saving binary file to disk.
- curl -d "" http://localhost:8080/test-insertion/copy-by-file-and-property/{count} - create data by copy method and KProperty map with saving file to disk.
- curl -d "" http://localhost:8080/test-insertion/copy-by-binary-file-and-property/{count} - create data by copy method and KProperty map with saving binary file to disk.
- curl -d "" http://localhost:8080/test-insertion/update-with-transaction/{count} - update data by sql script. Data will be saved on 100_000 rows.
- curl -d "" http://localhost:8080/test-insertion/update-by-property-with-transaction/{count} - update data by KProperty map and sql script. Data will be saved on 100_000 rows.