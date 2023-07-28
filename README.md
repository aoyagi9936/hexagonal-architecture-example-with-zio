## What is this repository?

This is a example repository of hexagonal architecture practiced with [ZIO](https://zio.dev/).  
Note that this is not a strict practice of the DDD pattern, as it is intended to commonize the GraphQL and Rest server application logic while separating of concerns (SoC) using the Caliban, Tapir and ZIO libraries.  

![Hexagonal Architecture Image](https://github.com/aoyagi9936/hexagonal-architecture-example-with-zio/blob/main/hexagonal_architecture.png)

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

### Start Server

```shell
sbt run
```

### GraphQL

```graphql
query {
  characters {
    characterId
    name
    nicknames
    origin
    role { 
      ... on Captain {
        shipName
      }
      ... on Engineer {
        shipName
      }
      ... on Mechanic {
        shipName
      }
      ... on Pilot {
        shipName
      }
    }
  }
}
```

### Rest API

```shell
curl -X 'GET' \
  'http://localhost:9000/api/v1/characters/list' \
  -H 'accept: application/json'
```

## ZIO HTTP

This repository currently uses http4s + ZIO as its HTTP server, the reason for not using zio-http is to support file uploads in Caliban[^1].  
If `ZHttpAdapter` supports file upload, it can be rewritten to zio-http.

[^1]:https://ghostdogpr.github.io/caliban/docs/adapters.html#built-in-adapters
