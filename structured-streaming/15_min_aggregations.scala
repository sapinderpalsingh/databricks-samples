import org.apache.spark.eventhubs.{ ConnectionStringBuilder, EventHubsConf, EventPosition }
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import com.microsoft.azure.cosmosdb.spark.schema._
import com.microsoft.azure.cosmosdb.spark.streaming.CosmosDBSinkProvider
import com.microsoft.azure.cosmosdb.spark.config.Config


// Create an event hub connection string
val connectionString = ConnectionStringBuilder("<YOUR EVENT HUB CONNECTION STRING>")
  .setEventHubName("<YOUR EVENT HUB NAME>")
  .build
val eventHubsConf = EventHubsConf(connectionString)
    .setConsumerGroup("$Default")
    .setStartingPosition(EventPosition.fromEndOfStream)

// Create a readstream by loading streaming data from eventhub    
val eventhubs = spark.readStream
  .format("eventhubs")
  .options(eventHubsConf.toMap)
  .load()

// Cosmosdb configurations
val configMap = Map(
"Endpoint" -> "<COSMOS DB ENDPOINT>",
"Masterkey" -> "<PRIMARY KEY>",
"Database" -> "eventsdb",
"Collection" -> "15_Min_Aggregates",
"Upsert" -> "true",
"WritingBatchSize" -> "500"
)

// Retreive the columns that you have to use in aggregations
val sensorDataSchema = StructType(Seq(StructField("PointId",StringType,true),
                                      StructField("TimeStamp",TimestampType,true),
                                      StructField("Temperature",DoubleType,true)))


var sensorDF = eventhubs.selectExpr("CAST(body as string)")
                        .select(from_json($"body",sensorDataSchema).as("sensorData"))
                            
sensorDF = sensorDF.select($"sensorData.PointId".alias("PointId"), to_timestamp($"sensorData.TimeStamp").alias("TimeStamp"), ($"sensorData.Temperature").cast("double").as("Temperature"))
//sensorDF.printSchema

var streamingAggregateDF = sensorDF.groupBy($"PointId",window($"TimeStamp", "15 minute").as("TimeStamp")) 
                                .agg(avg("Temperature").as("Avg"),
                                     max("Temperature").as("Max"),
                                     min("Temperature").as("Min"), 
                                     count("PointId").as("Count"))
                                


streamingAggregateDF = streamingAggregateDF.select((concat($"PointId",lit("_"),$"TimeStamp.start")).as("id"),
                                            $"PointId",
                                            $"TimeStamp.start".cast("string"),
                                            $"TimeStamp.end".cast("string"),
                                            $"Avg",
                                            $"Min",
                                            $"Max",
                                            $"Count")
var query = streamingAggregateDF
                            .writeStream
                            .format(classOf[CosmosDBSinkProvider].getName)
                            .outputMode("update")
                            .options(configMap)
                            .option("checkpointLocation", "/tmp/streamingAggregatesCheckPoint")
                            .start()


// Uncomment below line if you want to see the output on console.
// val query1 = streamingAggregateDF.writeStream.outputMode("complete").format("console").option("truncate", false).start().awaitTermination()