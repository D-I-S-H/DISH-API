# D.I.S.H API
## Dining Information Served Here

Backend for Dinner Information Served Here (DISH).  
Provides a REST API with MTU dinner menus from dineoncampus for quicker access from MTU's campus. Also provides account handling, and student ratings for individual meals.

---

### Prerequisites

- Java 17
- Gradle

### Building

1. **Clone the repository:**
    ```sh
    git clone git@github.com:D-I-S-H/DISH-API.git
    cd DISH-API
    ```

2. **Build the project using Gradle:**
    ```sh
    ./gradlew build
    ```

### Running

1. **Run the application:**
    ```sh
    ./gradlew bootRun
    ```

2. **Access the API:**
    - The API will be available at `http://localhost:8080`

### Testing

1. **Run the tests:**
    ```sh
    ./gradlew test
    ```

2. **View test reports:**
    - Test reports will be generated in `build/reports/tests/test/index.html`
