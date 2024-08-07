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
