# D.I.S.H API
## Dining Information Served Here

Backend for Dinner Information Served Here (DISH).  
Provides a REST API with MTU dinner menus from dineoncampus for quicker access from MTU's campus. Also provides account handling, and student ratings for individual meals.

## Table of Contents

- [Docker Quickstart](#docker-quickstart)
  - [Building with Docker Compose](#building-with-docker-compose)
  - [Building with Docker](#building-with-docker)
- [Prerequisites](#prerequisites)
- [Building](#building)
- [Running](#running)
- [Testing](#testing)

---

## Docker Quickstart

### Building with Docker Compose

To build and run the Docker image using Docker Compose, use:

```sh
docker-compose up --build
```

### Building with Docker

```sh
docker run -d --name dish-api -p 8080:8080 ghcr.io/d-i-s-h/dish-api:latest
```

---

### Prerequisites

- Java 17
- Gradle

## Building

1. **Clone the Repository**:

    ```sh
    git clone git@github.com:D-I-S-H/DISH-API.git
    cd DISH-API
    ```

2. **Build the Project using Gradle**:

    ```sh
    ./gradlew build
    ```

## Running

1. **Run the Application**:

    ```sh
    ./gradlew bootRun
    ```

    > **Note:** Ensure you re-run the Java application to regenerate the database before executing `chartwells_query.py`.

2. **Access the API**:
    - The API will be available at `http://localhost:8080`

## Testing

1. **Run the Tests**:

    ```sh
    ./gradlew test
    ```

2. **View Test Reports**:
    - Test reports are generated at `build/reports/tests/test/index.html`