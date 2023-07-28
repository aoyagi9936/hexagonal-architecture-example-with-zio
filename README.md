## What is this repository?

This is a sample repository of hexagonal architecture practiced with [ZIO](https://zio.dev/).  
Note that it is intended to efficiently standardize the API implementation of the GraphQL and Rest servers using the Caliban and Tapir libraries, and does not strictly practice the DDD pattern.  

## Folder Structure Summary

Start each server in `Main.scala` and inject dependencies in `AppContext.scala` .  
Ports separate the adapter from the application.  
Definitions and implementations are also separated within the adapter (this is a Caliban/Tapir feature regardless of the hexagonal architecture).  
In all, the ZIO framework functions as a type-safe composable asynchronous process!  

```
.
├── build.sbt
└── gateway/
    ├── Main.scala
    ├── adapters/
    │   ├── primary
    │   └── secondary
    ├── application/
    │   ├── core/
    │   │   └── AppContext.scala
    │   ├── config
    │   ├── models
    │   └── services
    └── ports/
        ├── primary
        └── secondary
```

## Application Launch

This application requires Scala 3.2.2.  
Running the application will start the GraphQL server on port 8088 and the Rest server on port 9000.  
The GraphQL IDE is available by accessing `http://localhost:8088/graphiql` .  
The Swagger UI is available by accessing `http://localhost:9000/docs` .  

```shell
sbt run
```

## ZIO HTTP

This repository currently uses http4s + ZIO as its HTTP server, the reason for not using zio-http is to support file uploads in Caliban[^1].  
If `ZHttpAdapter` supports file upload, it can be rewritten to zio-http.

[^1]:https://ghostdogpr.github.io/caliban/docs/adapters.html#built-in-adapters
