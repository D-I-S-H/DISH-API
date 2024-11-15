# D.I.S.H API
## Dining Information Served Here 
This project provides a REST API with menus for the dining halls at MTU.  
Data is retrieved from the [dineoncampus](https://dineoncampus.com/) API and automatically curated in order to provide faster access.  

This project is built alongside the [DISH Frontend](https://github.com/D-I-S-H/DISH-FE), which provides a web interface for the data.




## Table of Contents

- [Docker Quickstart](#docker-quickstart)
  - [Building with Docker Compose](#building-with-docker-compose)
  - [Building with Docker](#building-with-docker)
- [Prerequisites](#prerequisites)
- [Building](#building)
- [Running](#running)
- [Retrieving Data](#retrieving-data)
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
- Python 3

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

## Retrieving Data
In order to populate the database with the latest data, run the following command:

```sh
python chartwells_query.py
```


## Testing

1. **Run the Tests**:

    ```sh
    ./gradlew test
    ```

2. **View Test Reports**:
    - Test reports are generated at `build/reports/tests/test/index.html`