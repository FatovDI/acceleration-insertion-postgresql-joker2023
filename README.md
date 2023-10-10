# Acceleration Insertion for PostgreSQL - Joker2023 🚀

## **Description**

This application is designed to test various methods for inserting data into a PostgreSQL database. It's built with:
- A Kotlin-based backend.
- A preloaded PostgreSQL database with over 100 million rows in the "payment_document" table.
- Multiple indexes in the "payment_document" table to influence the insertion process.

## **Prerequisites**

- **Storage:** Ensure you have at least 32GB of free space on your machine to work with the preloaded database.

## **Working with the Preloaded Database**

1. **Download the Image:** Grab the PostgreSQL image with preloaded data [here](https://disk.yandex.ru/d/lzqUyby5aIFadw) named `db_joker.tar.gz`.
2. **Load Image:** Import the image to your local Docker registry using:
   ```bash
   sudo docker load < db_joker.tar.gz
   ```
3. **Start Application:** Using git bash:
   ```bash
   sh start.sh
   ```
4. **Stop Application:** Using git bash:
   ```bash
   sh stop.sh
   ```

## **Working with an Empty Database**

1. **Configure Image:** Update `docker-compose.yaml` and change the line:
   ```yaml
   image: db_joker:1
   ```
   to:
   ```yaml
   image: postgres:latest
   ```
2. **Start Application:**
   ```bash
   sudo docker-compose up -d --build
   ```
3. **Stop Application:** Using git bash:
   ```bash
   sudo docker-compose down -v
   ```

## **API Endpoints**

Use the following endpoints to test different insertion methods. You can specify the number of rows to generate in the database and retrieve results with performance metrics. For convenience, utilize `curl -X POST` to interact with these endpoints. The number of rows to create is specified by the `count` path parameter.

```bash
# Insert using Spring
http://localhost:8080/test-insertion/spring/{count}

# Insert using Spring and Copy with file saving
http://localhost:8080/test-insertion/spring-with-copy/{count}

# Update using Spring (Data saved on 100,000 rows)
http://localhost:8080/test-insertion/spring-update/{count}

# Create data using the INSERT method. The data will be saved in batches of 100,000 rows.
http://localhost:8080/test-insertion/insert/{count} - create data by insert method. Data will be saved on 100_000 rows.

# Create data using the INSERT method with KProperty map.
http://localhost:8080/test-insertion/insert-by-property/{count}

# Create data using the INSERT method with dropping index before transaction and recreating it after that. The data will be saved in batches of 100,000 rows.
http://localhost:8080/test-insertion/insert-with-drop-index/{count}

# Create data using the COPY method without saving file to disk. The data will be saved in batches of 100,000 rows.
http://localhost:8080/test-insertion/copy/{count}

# Create data with KProperty map using the COPY method without saving file to disk.
http://localhost:8080/test-insertion/copy-by-property/{count}

# Create data using the COPY method with binary transformation. All data will be saved in one transaction.
http://localhost:8080/test-insertion/copy-by-binary/{count}

# Create data with KProperty map using the COPY method with binary transformation.
http://localhost:8080/test-insertion/copy-by-binary-and-property/{count}

# Create data using the COPY method with saving file to disk.
http://localhost:8080/test-insertion/copy-by-file/{count}

# Create data using the COPY method with saving binary file to disk.
http://localhost:8080/test-insertion/copy-by-binary-file/{count}

# Create data using the COPY method and KProperty map with saving file to disk.
http://localhost:8080/test-insertion/copy-by-file-and-property/{count}

# Create data using the COPY method and KProperty map with saving binary file to disk.
http://localhost:8080/test-insertion/copy-by-binary-file-and-property/{count}

# Update data using SQL script.
http://localhost:8080/test-insertion/update/{count}

# Update data using KProperty map and SQL script. 
http://localhost:8080/test-insertion/update-by-property/{count}
```