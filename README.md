# Dongseo University Spring Boot Demo

---
This API is for creating, reading, updating, and deleting an array of rooms in the university and managing their reservations.

Claude Opus 5.5 was used for Java questions and consulting. Code is written by hand.

## Authorization

---
Accounts live in `app_user` table. Passwords are stored as BCrypt hashes. 2 roles for any account: `USER` or `ADMIN`. Signing up always gives `USER`, even if a request asks otherwise.

### Logging in

There are 2 ways to authenticate:

1. **HTTP Basic:** send `Authorization: Basic <base64 of username:password>` with every request.
2. **JWT (Bearer token):** call `POST /api/auth/login` once with `{ "username": "...", "password": "..." }`. The answer contains an `accessToken`, valid for 30 minutes (`app.jwt.ttl-minutes`). Send it as `Authorization: Bearer <accessToken>` on later requests.

The token is signed with HS256 using `JWT_SECRET` from `.env`. It carries the username (`sub`) and the role (`role`), which Spring turns into `ROLE_USER` or `ROLE_ADMIN`.

### Rules

| Rule | Who                                            | Where it is checked                 |
|------|------------------------------------------------|-------------------------------------|
| A    | Anyone may read rooms                          | `SecurityConfig`                    |
| B    | Reservations need a login                      | `SecurityConfig`                    |
| C    | A reservation belongs to its owner             | `AppointmentService` (needs the DB) |
| D    | Only `ADMIN` creates, changes or deletes rooms | `SecurityConfig`                    |

`SecurityConfig` can only see the method, the URL and the role. Ownership (rule C) needs the reservation row, so the service loads it first (404 if missing), then compares the owner (403 if belongs to someone else).

## Endpoint table

---
| Method | Path                       | Status                            | Purpose                                                                |
|--------|----------------------------|-----------------------------------|------------------------------------------------------------------------|
| GET    | /api/{id}/name             | HTTP/1.1 200 OK                   | Get room name of room at id                                            |
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

## Appointment endpoint table

---
| Method | Path                                 | Status                            | Purpose                                                              |
|--------|--------------------------------------|-----------------------------------|----------------------------------------------------------------------|
| GET    | /api/rooms/1/reservations            | HTTP/1.1 200 OK                   | List all appointments of room 1, ordered by start time               |
| GET    | /api/rooms/1337/reservations         | HTTP/1.1 404 NOT FOUND            | List appointments of room 1337 (non-existent)                        |
| GET    | /api/reservations?appointedBy=Mina   | HTTP/1.1 200 OK                   | List all appointments made by Mina, with room names                  |
| GET    | /api/reservations?appointedBy=Nobody | HTTP/1.1 404 NOT FOUND            | List appointments of someone who has none                            |
| GET    | /api/reservations                    | HTTP/1.1 400 BAD REQUEST          | Search without the `appointedBy` query parameter                     |
| GET    | /api/reservations/1                  | HTTP/1.1 200 OK                   | Get appointment at id 1                                              |
| GET    | /api/reservations/1337               | HTTP/1.1 404 NOT FOUND            | Get appointment at id 1337 (non-existent)                            |
| POST   | /api/rooms/1/reservations            | HTTP/1.1 201 CREATED / ...headers | Book room 1; `Location` points to `/api/reservations/{id}`           |
| POST   | /api/rooms/1/reservations            | HTTP/1.1 400 BAD REQUEST          | Book room 1 with blank `appointedBy`, missing time, or end < start   |
| POST   | /api/rooms/1/reservations            | HTTP/1.1 409 CONFLICT             | Book room 1 at a time that overlaps an existing appointment          |
| POST   | /api/rooms/1337/reservations         | HTTP/1.1 404 NOT FOUND            | Book room 1337 (non-existent)                                        |
| DELETE | /api/reservations/1                  | HTTP/1.1 204 EMPTY                | Cancel appointment at id 1                                           |
| DELETE | /api/reservations/1337               | HTTP/1.1 404 NOT FOUND            | Cancel appointment at id 1337 (non-existent)                         |

## Database

This project uses [MySQL](https://www.mysql.com/) to store surviving data.

`reset.sql` builds the two tables and fills them with data.

### Table `demo`

| Column     | Type         | Constraints                      |
|------------|--------------|----------------------------------|
| `id`       | BIGINT       | Primary key, auto-generated      |
| `name`     | VARCHAR(100) | NOT NULL                         |
| `capacity` | INT          | NOT NULL, CHECK between 1 and 20 |

### Table `appointment`

| Column         | Type         | Constraints                         |
|----------------|--------------|-------------------------------------|
| `id`           | BIGINT       | Primary key, auto-generated         |
| `demo_id`      | BIGINT       | NOT NULL, foreign key to `demo(id)` |
| `appointed_by` | VARCHAR(100) | NOT NULL                            |
| `start_time`   | DATETIME     | NOT NULL                            |
| `end_time`     | DATETIME     | NOT NULL                            |

### Relationship

| Parent table | Child table   | Join column                       | Cardinality                            |
|--------------|---------------|-----------------------------------|----------------------------------------|
| `demo`       | `appointment` | `appointment.demo_id` = `demo.id` | One room has many appointments (1 : N) |

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
*NOTE: linux users might experience unexpected behavior when using `localhost` to loopback instead of 127.0.0.1*

4. Run `reset.sql`:
```mysql
use demodb;
source /path/to/root/of/project/reset.sql;
```

The web layer returns 409 through `ApiExceptionHandler`, because HTTP status codes belong here, so the handler only translates exceptions thrown by the database.

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

## Tests

---
| Class                         | Kind                         | What it checks                                                                                   |
|-------------------------------|------------------------------|--------------------------------------------------------------------------------------------------|
| `ReservationServiceTest`      | Service test (JUnit+Mockito) | `AppointmentService` rules (400, 404, 403, 409, owner/admin) with fake repositories. No database |
| `ApiFailureTest`              | API test (MockMvc)           | The whole app: status code and `ApiError` message for 401, 403, 409, 400, 404                    |
| `ServerHelloApplicationTests` | Native test                  | The application context starts                                                                   |

### Before running

`ApiFailureTest` & `ServerHelloApplicationTests` need:

1. **MySQL running**, with the `demodb` database and the `demoapp` user (see *Setting up MySQL*).
2. **The `reset.sql` data.** The tests expect exact match: **mina owns reservation 1, room 1 has reservations, and the accounts are mina, omar and admin.**
3. **An `.env` file** in the project root with `DB_PASSWORD`, `JWT_SECRET` and `CORS_ORIGINS` (see `.env.example`).

Each API test is `@Transactional` and rolled back, so running the tests does not change the data.
`ReservationServiceTest` does not use Spring or the database, so it needs no ceremony.

### Running

From the project root:

### Windows

```ps1
.\gradlew.bat test
```

### Linux

```bash
./gradlew test
```

All tests must pass. Report is stored at `build/reports/tests/test/index.html`.

Latest compiled report can be found [HERE](src/test/java/com/dongseo/server_hello/test_report.html)


## Dependencies

---
- [Java Development Kit 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [MySQL 8.4.11](https://dev.mysql.com/downloads/mysql/8.4.html)

## How to run

---
**OPTION 1**: From the project root (server-hello), in PowerShell or cmd:

### Windows

```ps1
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

```ps1
.\gradlew.bat bootJar
```

### Linux

```bash
./gradlew bootJar
```

Run the .jar:

### Windows

```ps1
java -jar build\libs\server-hello-2.0.jar
```

### Linux

```bash
java -jar build/libs/server-hello-2.0.jar
```

*Note: version may change. Check build.gradle in build\libs (after running the first line)*
