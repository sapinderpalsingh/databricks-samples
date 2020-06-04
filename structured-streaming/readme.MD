## Read streaming data from eventhub, do aggregations and save the aggregations to cosmosdb (SQL API).

# Pre-requistes
* Eventhub
* Databricks cluster
* Cosmosdb (SQL API)

Install the following libraries to your databricks cluster:

[Apache Spark Cosmosdb connector](https://aka.ms/CosmosDB_OLTP_Spark_2.4_LKG)

[Apache Spark Eventhub connector](https://github.com/Azure/azure-event-hubs-spark)

You can use the following .Net Core console app to stream the sample data to event hub.

[Event Simulator](https://github.com/sapinderpalsingh/EventSimulator)

Sample json format used for this sample is:

```json
{
    "Id":32672,
    "ClientId":"5f089049-5e9f-4ede-be5e-5b2d895e98b0",
    "PointId":"2fcc96ae-c1f4-45e8-ba54-c989e602da39",
    "Temperature":16.43169287658841,
    "Humidity":55.13038852025307,
    "TimeStamp":"2020-06-03T20:40:22.6688228+05:30"
}

```
Outcome of the sample is that you will have a 15 minute temperature aggrgations stored in cosmos against every point id for the streamed data coming in eventhub:

```json
[
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 12:45:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.90977631174539,
        "Min": 15.095504319805421,
        "Max": 29.984371899619873,
        "Count": 577
    },
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 13:00:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.349556488412453,
        "Min": 15.011355250613464,
        "Max": 29.97601003617794,
        "Count": 628
    },
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 13:15:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.305645141130913,
        "Min": 15.001786400099185,
        "Max": 29.96918469433169,
        "Count": 649
    },
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 13:30:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.17268786321605,
        "Min": 15.025721595168916,
        "Max": 29.99651064164774,
        "Count": 627
    },
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 13:45:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.341919158898033,
        "Min": 15.02756512492316,
        "Max": 29.979712860649318,
        "Count": 613
    },
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 14:00:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.46704893170468,
        "Min": 15.005713643043169,
        "Max": 29.98643507714683,
        "Count": 650
    },
    {
        "id": "0e66d5f6-6110-468b-b0be-54c6c95d68a5_2020-06-03 14:15:00",
        "PointId": "0e66d5f6-6110-468b-b0be-54c6c95d68a5",
        "Avg": 22.446842660669304,
        "Min": 15.003550581170037,
        "Max": 29.984521593425665,
        "Count": 606
    }
]
```
