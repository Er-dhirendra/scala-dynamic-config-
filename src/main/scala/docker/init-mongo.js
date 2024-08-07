// Initialize the replica set
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "localhost:27017" }
  ]
});

// Optionally, you can add data to your database here
db = db.getSiblingDB('mydatabase');

// Create collections and insert sample data
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
