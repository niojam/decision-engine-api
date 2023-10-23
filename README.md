# Inbank decision-engine


## Requirements

For building and running the application you need:

- [JDK 21](https://jdk.java.net/21/)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `ee/inbank/decisionengine/DecisionEngineApplication.java` class from your IDE.

Alternatively you can use the gradle wrapper like so:

```shell
./gradlew bootRun
```

Endpoint can be triggered via swagger-ui or front end application

`http://localhost:8080/swagger-ui/index.html`


## App limitations:

1. Customer profiles are mocked by CustomerProfileProvider.
2. Euro as a currency supported only.
3. Estonian personal codes supported only.
4. App does not try to find suitable loan option by reducing both loan period and loan amount (not sure if it was in scope of the task).
5. Loan request (applications) and decisions are not saved, as we do not have database. (not sure if it was in scope of the task).
6. No authentication and authorization

## Notes (order is random)

Notes regarding validation 

was considering using Min(value) & Max(value) for input parameter validations, but decided to stick with custom validators in scope of this small app to make limits configurable from one sorce of truth (application.propeties)

Notes regarding DecisionMaker implementation

Initial idea while reading the task was using camunda-engine-dmn or OpenL Tablets, but decided to implement algorithm in java code.
Can discuss it during tech interview :)

Notes regarding packaging

Prefer feature based packaging personally, therefore customer provider is implemented in separate package, Assuming that in the future there can be done some customer-managment as well if we do not really want to stick with microservice concept.
Of course can be done just by adding Client class in `loan` package.
