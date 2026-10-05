# Dongseo University Spring Boot Demo

---
This API is for creating, reading, updating, and deleting an array of rooms in the university.

Claude Opus 5.5 was used for Java questions and consulting. Code is written by hand.

## Endpoint table

---
| Method | Path                       | Status                            | Purpose                                                                |
|--------|----------------------------|-----------------------------------|------------------------------------------------------------------------|
| GET    | /api/{id}                  | HTTP/1.1 200 OK                   | Get room name of room at id                                            |
| GET    | /api/search?keyword=STRING | HTTP/1.1 200 OK                   | Get first room matching keyword in name                                |
| GET    | /api/rooms                 | HTTP/1.1 200 OK                   | List all rooms                                                         |
| GET    | /api/rooms/{id}            | HTTP/1.1 200 OK                   | Get room at id                                                         |
| GET    | /api/rooms/1337            | HTTP/1.1 404 NOT FOUND            | Get room at id 1337 (non-existent) for testing                         |
| GET    | /api/rooms?minCapacity=40  | HTTP/1.1 200 OK                   | List all rooms with minimum capacity of 40                             |
| GET    | /api/rooms?minCapacity=999 | HTTP/1.1 404 NOT FOUND            | List all rooms with minimum capacity of 999 (non-existent) for testing |
| GET    | /api/rooms?keyword=s       | HTTP/1.1 200 OK                   | Get all rooms matching keyword in name                                 |
| POST   | /api/rooms                 | HTTP/1.1 201 CREATED / ...headers | Create a room and append to end of list                                |
| POST   | /api/rooms                 | HTTP/1.1 400 BAD REQUEST          | Create a room with out of bounds capacity                              |
| PUT    | /api/rooms/2               | HTTP/1.1 200 OK                   | Replace room at id 2                                                   |
| PUT    | /api/rooms/1337            | HTTP/1.1 404 NOT FOUND            | Replace a room at id 1337 (non-existent) for testing                   |
| PUT    | /api/rooms/2               | HTTP/1.1 400 BAD REQUEST          | Replace room at id 2 with one whose capacity is out of bounds          |
| DELETE | /api/2                     | HTTP/1.1 204 NO CONTENT           | Delete room at id 2                                                    |
| DELETE | /api/{id}                  | HTTP/1.1 404 NOT FOUND            | Delete room at id 1337 (non-existent) for testing                      |

## Database

This app uses an [in-memory H2 database](https://h2database.com/html/main.html).

`schema.sql` creates the tables while `data.sql` fills them with data each start:

### Table `demo`

| Column     | Type         | Constraints                      |
|------------|--------------|----------------------------------|
| `id`       | BIGINT       | Primary key, auto-generated      |
| `name`     | VARCHAR(100) | NOT NULL                         |
| `capacity` | INT          | NOT NULL, CHECK between 1 and 20 |

### Table `reservation`

| Column        | Type        | Constraints                         |
|---------------|-------------|-------------------------------------|
| `id`          | BIGINT      | Primary key, auto-generated         |
| `demo_id`     | BIGINT      | NOT NULL, foreign key to `demo(id)` |
| `reserved_by` | VARCHAR(50) | NOT NULL                            |
| `start_time`  | TIMESTAMP   | NOT NULL                            |
| `end_time`    | TIMESTAMP   | NOT NULL                            |

### Relationship

| Parent table | Child table   | Join column                       | Cardinality                            |
|--------------|---------------|-----------------------------------|----------------------------------------|
| `demo`       | `reservation` | `reservation.demo_id` = `demo.id` | One room has many reservations (1 : N) |

One reservation can only belong to one room (1:N)

## H2 Console

Run the project, then open the console in a browser:

| Setting      | Value                                  |
|--------------|----------------------------------------|
| Console URL  | http://127.0.0.1:8080/h2-console       |
| JDBC URL     | `jdbc:h2:mem:demodb`                   |
| User Name    | `sa`                                   |
| Password     | (empty)                                |

*NOTE: The JDBC URL must match `spring.datasource.url` in `application.properties`*

Example queries are in `queries.sql`

Claude Sonnet 5.5 was used for a refresher on SQL statements. Queries written by hand

## Dependencies

---
- [Java Development Kit 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

## How to run

---
**OPTION 1**: From the project root (server-hello), in PowerShell or cmd:

### Windows

```bash
.\gradlew.bat bootRun
```

### Linux

```bash
./gradlew bootRun
```

This downloads Gradle on first run, then starts server on port `8080`. `Ctrl+C`, enter, `y`, enter to stop

---
**OPTION 2**: Building standalone jar and running it:

Build the .jar:

### Windows

```bash
.\gradlew.bat bootJar
```

### Linux

```bash
./gradlew bootJar
```

Run the .jar:

### Windows

```bash
java -jar build\libs\server-hello-1.2.jar
```

### Linux

```bash
java -jar build/libs/server-hello-1.2.jar
```

*Note: version may change. Check build.gradle in build\libs (after running the first line)*
