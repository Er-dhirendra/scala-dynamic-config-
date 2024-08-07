
---

# Dynamic Config Reader

This project is a Scala application that connects to a MongoDB database, watches for changes using MongoDB Change Streams, and reacts to those changes in real-time. It includes Docker and Docker Compose configurations for easy setup.

## Project Structure

```
.
├── Dockerfile
├── docker-compose.yml
├── init-mongo.js
├── src
│   ├── main
│   │   └── scala
│   │       ├── DBUtils
│   │       │   ├── DBUtils.scala
│   │       │   └── MongoUtils.scala
│   │       └── DynamicConfigReader.scala
└── README.md
```

## Requirements

- Docker
- Docker Compose
- Scala
- sbt (Scala Build Tool)

## Setup

### 1. Clone the Repository

First, clone this repository to your local machine:

```bash
git clone https://github.com/your-repository/dynamic-config-reader.git
cd dynamic-config-reader
```

### 2. Install Docker and Docker Compose

Follow the instructions for your operating system to install Docker and Docker Compose:

- [Docker Installation](https://docs.docker.com/get-docker/)
- [Docker Compose Installation](https://docs.docker.com/compose/install/)

### 3. Build and Run MongoDB with Docker Compose

MongoDB is configured to run as a replica set using Docker Compose. To set it up, run:

```bash
docker-compose up
```

This command will build and start the MongoDB container, apply the `init-mongo.js` script to initialize the database, and set up the replica set.

## Running the Application

### 1. Navigate to the Project Directory

Ensure you are in the root directory of the project where `build.sbt` is located.

### 2. Compile and Run the Scala Application

Use sbt to compile and run the Scala application:

```bash
sbt run
```

The application will start and listen for changes in the MongoDB `Config` collection. Any detected changes will be printed to the console.

## Configuration Files

### Dockerfile

The Dockerfile is used to create a Docker image for MongoDB with the initialization script.

```dockerfile
# Use the official MongoDB image
FROM mongo:latest

# Copy the initialization script into the Docker container
COPY init-mongo.js /docker-entrypoint-initdb.d/

# Expose MongoDB port
EXPOSE 27017
```

### docker-compose.yml

The Docker Compose file defines the MongoDB service with a replica set.

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:latest
    container_name: mongodb
    ports:
      - "27017:27017"
    volumes:
      - ./init-mongo.js:/docker-entrypoint-initdb.d/init-mongo.js
    command: ["mongod", "--replSet", "rs0"]
```

### init-mongo.js

The initialization script configures the MongoDB replica set and inserts sample data.

```javascript
// Initialize the replica set
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "localhost:27017" }
  ]
});

// Create collections and insert sample data
db = db.getSiblingDB('mydatabase');

db.Config.insertMany([
  {
    _id: "databaseConfig",
    database: {
      host: "localhost",
      port: 5432,
      username: "admin",
      password: "secret"
    }
  },
  {
    _id: "serverConfig",
    server: {
      host: "0.0.0.0",
      port: 8080
    }
  },
  {
    _id: "loggingConfig",
    logging: {
      level: "info"
    }
  }
]);

// Check the replica set status to ensure it is running correctly
printjson(rs.status());
```

### DynamicConfigReader.scala

The Scala application connects to MongoDB and listens for changes in the `Config` collection.

```scala
import DBUtils.MongoUtils
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonDocument

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object DynamicConfigReader extends App {

  private val changeStream: ChangeStreamObservable[BsonDocument] = MongoUtils.collection.watch[BsonDocument]()

  val subscription = changeStream.subscribe(
    (change) => {
      println(s"Change detected: ${change.getFullDocument.toJson}")
    },
    (error: Throwable) => {
      println(s"Error: ${error.getMessage}")
    },
    () => {
      println("Change stream completed")
    }
  )

  // Keep the application running to listen to changes
  Await.result(Future.never, Duration.Inf)

  // Close the client when done (this is a placeholder; actual close will not be reached in this example)
  MongoUtils.client.close()
}
```