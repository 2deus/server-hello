# Dongseo University Spring Boot Demo

---
This API is for creating, reading, updating, and deleting an array of rooms in the university.

Claude Opus 5.5 was used for Java questions and consulting. Code is written by hand.

## Endpoint table

---
| Method | Path                       | Status                            | Purpose                                                                |
|--------|----------------------------|-----------------------------------|------------------------------------------------------------------------|
| GET    | /api/{id}                  | HTTP/1.1 200 OK                   | Get room name of room at id                                            |
| GET    | /api/rooms                 | HTTP/1.1 200 OK                   | List all rooms                                                         |
| GET    | /api/rooms/{id}            | HTTP/1.1 200 OK                   | Get room at id                                                         |
| GET    | /api/rooms/1337            | HTTP/1.1 404 NOT FOUND            | Get room at id 1337 (non-existent)                                     |
| GET    | /api/rooms?minCapacity=10  | HTTP/1.1 200 OK                   | List all rooms with minimum capacity of 10                             |
| GET    | /api/rooms?minCapacity=999 | HTTP/1.1 404 NOT FOUND            | List all rooms with minimum capacity of 999 (non-existent)             |
| GET    | /api/rooms?keyword=s       | HTTP/1.1 200 OK                   | Get all rooms matching keyword in name                                 |
| GET    | /api/rooms/count           | HTTP/1.1 200 OK                   | Print the number of rooms with capacity over 10                        |
| POST   | /api/rooms                 | HTTP/1.1 201 CREATED / ...headers | Create a room and append to end of list                                |
| POST   | /api/rooms                 | HTTP/1.1 400 BAD REQUEST          | Create a room with out of bounds capacity                              |
| PUT    | /api/rooms/2               | HTTP/1.1 200 OK                   | Replace room at id 2                                                   |
| PUT    | /api/rooms/1337            | HTTP/1.1 404 NOT FOUND            | Replace a room at id 1337 (non-existent)                               |
| PUT    | /api/rooms/2               | HTTP/1.1 400 BAD REQUEST          | Replace room at id 2 with one whose capacity is out of bounds          |
| DELETE | /api/rooms/3               | HTTP/1.1 204 EMPTY                | Delete a room at id 3                                                  |
| DELETE | /api/2                     | HTTP/1.1 409 CONFLICT             | Delete a room at id 2, which has reservations                          |
| DELETE | /api/{id}                  | HTTP/1.1 404 NOT FOUND            | Delete room at id 1337 (non-existent)                                  |

## Database

This project uses [MySQL](https://www.mysql.com/) to store surviving data.

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

## Setting up MySQL

---
Before running the project, you must set up the database.

After having logged in as root:
1. Create the database:
```mysql
CREATE DATABASE demodb;
```
2. Create the user (exactly as defined in application.properties):
```mysql
CREATE USER 'demoapp'@'localhost' IDENTIFIED BY 'demoapp1234';
```
3. Grant all privileges to this database for that user:
```mysql
GRANT ALL PRIVILEGES ON demodb.* TO 'demoapp'@'localhost';
FLUSH PRIVILEGES;
```
4. Run `reset.sql`:
```mysql
source /path/to/root/of/project/reset.sql
```

The web layer returns 409 through `DemoExceptionHandler`, because HTTP status codes belong here, so the handler only translates exceptions thrown by the database.

Hibernate:
```mysql
select d1_0.id, d1_0.capacity, d1_0.name
    from demo d1_0
    where d1_0.id=?;
    
update demo
    set capacity=?, name=?
    where id=?
```

The select loads the existing room by id (findById), and the update then writes the new name and capacity back because they differ from the loaded row (save).

*NOTE: Claude Sonnet 5.5 helped me come up with the idea of `DemoExceptionHandler.java` and guided me through realizing it.*

## Dependencies

---
- [Java Development Kit 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [MySQL 8.4.11](https://dev.mysql.com/downloads/mysql/8.4.html)

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
