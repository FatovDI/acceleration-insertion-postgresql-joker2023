# acceleration-insertion-postgresql-joker2023

Description:
This is tested application for testing different approaches insertion data to PostgresSQL
The application has a backend which written on kotlin and a prepared postgres DB with 100_000_000 rows in a general table "payment_document".
The table "payment_document" has several index to do affect on insert data.

Prerequisite:
1. Your computer should have at last 32 Gb free space for run postrges with prepared data. Because this one included 100_000_000 rows table "payment_document".

Preparation:
1. Download prepared image postgres with data >>> db_joker.tar.gz
2. Load this image to your local registry docker load <  db_joker.tar
3. Before build db_joker image please set up this setting
   export DOCKER_CLIENT_TIMEOUT=600
   export COMPOSE_HTTP_TIMEOUT=600
4. Save docker-compose.yaml abd run docker-compose -f docker-compose.yaml up -d
5. TODO Create one file for backend end DB

Working:
The application has several endpoints for testing different approaches to insert data. 
All of them you can set count of data to generate row in DB and get result with measurements.
I advise call these endpoints by curl.

Endpoints:
- POST http://localhost:8080/test-insertion/spring/{count} - create data by spring.
- POST http://localhost:8080/test-insertion/insert-with-transaction/{count} - create data by insert method. All data will be saved in one transaction.
- POST http://localhost:8080/test-insertion/insert/{count} - create data by insert method. Data will be saved on 100_000 rows.
- POST http://localhost:8080/test-insertion/insert-with-drop-index/{count} - create data by insert method with drop index before transaction and recreating this one after that.
- POST http://localhost:8080/test-insertion/copy-with-transaction/{count} - create data by copy method without saving file to disk. All data will be saved in one transaction.
- POST http://localhost:8080/test-insertion/copy/{count} - create data by copy method without saving file to disk. Data will be saved on 100_000 rows.
- POST http://localhost:8080/test-insertion/copy-by-file/{count} - create data by copy method with saving file to disk.