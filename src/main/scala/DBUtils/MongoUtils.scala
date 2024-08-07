package DBUtils

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

object MongoUtils extends DBUtils {

  val uri = "mongodb://localhost:27017"
  // Create a MongoClient
  val client: MongoClient = MongoClient(uri)

  // Access the database and collection
  val database: MongoDatabase = client.getDatabase("mydatabase")
  val collection: MongoCollection[BsonDocument] = database.getCollection("Config")
}
