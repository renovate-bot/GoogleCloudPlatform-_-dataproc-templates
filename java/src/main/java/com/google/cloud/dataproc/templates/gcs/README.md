## 1. Cloud Storage To BigQuery

General Execution:

```
GCP_PROJECT=<gcp-project-id> \
REGION=<region>  \
SUBNET=<subnet>   \
GCS_STAGING_LOCATION=<gcs-staging-bucket-folder> \
HISTORY_SERVER_CLUSTER=<history-server> \
bin/start.sh \
-- --template GCSTOBIGQUERY \
--templateProperty project.id=<gcp-project-id> \
--templateProperty gcs.bigquery.input.location=<gcs path> \
--templateProperty gcs.bigquery.input.format=<csv|parquet|avro|orc> \
--templateProperty gcs.bigquery.output.dataset=<datasetId> \
--templateProperty gcs.bigquery.output.table=<tableName> \
--templateProperty gcs.bigquery.output.mode=<Append|Overwrite|ErrorIfExists|Ignore> \
--templateProperty gcs.bigquery.temp.bucket.name=<bigquery temp bucket name>
```

There are two optional properties as well with "Cloud Storage to BigQuery" Template. Please find below the details :-

```
--templateProperty gcs.bigquery.temp.table='temporary_view_name'
--templateProperty gcs.bigquery.temp.query='select * from global_temp.temporary_view_name'
```
These properties are responsible for applying some spark sql transformations while loading data into BigQuery.
The only thing needs to keep in mind is that, the name of the Spark temporary view and the name of table in the query should match exactly. Otherwise, there would be an error as:- "Table or view not found:"

## 2. Cloud Storage To BigTable

Please refer our public [documentation](https://cloud.google.com/bigtable/docs/use-bigtable-spark-connector) for more details around spark bigtable connector.

General Execution:

`Catalog` Example : 

Make sure `table name` matches with your existing BigTable table name if you are working with existing table. If you try to create a brand new table then `employee` table will be created. 

Note: We support table creation only first time when it is not created otherwise connector will throw `table already exist` error.
```json
{
  "table": {"name": "employee"},
  "rowkey": "id_rowkey",
  "columns": {
    "key": {"cf": "rowkey", "col": "id_rowkey", "type": "string"},
    "name": {"cf": "personal", "col": "name", "type": "string"},
    "address": {"cf": "personal", "col": "address", "type": "string"},
    "empno": {"cf": "professional", "col": "empno", "type": "string"}
  }
}
```

```
GCP_PROJECT=<gcp-project-id> \
REGION=<region>  \
SUBNET=<subnet>   \
GCS_STAGING_LOCATION=<gcs-staging-bucket-folder> \
HISTORY_SERVER_CLUSTER=<history-server> \
bin/start.sh \
-- --template GCSTOBIGTABLE \
--templateProperty project.id=<gcp-project-id> \
--templateProperty gcs.bigtable.input.location=<gcs file location> \
--templateProperty gcs.bigtable.input.format=<csv|parquet|avro> \
--templateProperty gcs.bigtable.output.instance.id=<bigtable instance Id> \
--templateProperty gcs.bigtable.output.project.id=<bigtable project Id> \
--templateProperty gcs.bigtable.catalog.location="gs://dataproc-templates/conf/employeecatalog.json"
```

Additional supported parameters.

1. `spark.bigtable.create.new.table` : Set to true if you would like to create a table based on your catalog. Default is `false`.
2. `spark.bigtable.batch.mutate.size` : The batch size for batch mutation requests. Maximum is `100K`. Default is `100`.

## 3. Cloud Storage to Spanner
Spanner JDBC driver is supporting GoogleSQL and Postgresql dialect. Please refer our [documentation](https://cloud.google.com/spanner/docs/pg-jdbc-connect#spanner-jdbc-driver). Template by default uses GoogleSQL dialect.
<br/><b>GoogleSQL dialect Example:</b>
```
GCP_PROJECT=<gcp-project-id> \
REGION=<region>  \
GCS_STAGING_LOCATION=<gcs-staging-bucket-folder> \
bin/start.sh \
-- --template GCSTOSPANNER \
--templateProperty project.id=<gcp-project-id> \
--templateProperty gcs.spanner.input.format=<avro | parquet | orc | csv> \
--templateProperty gcs.spanner.input.location=<gcs path> \
--templateProperty gcs.spanner.output.instance=<spanner instance id> \
--templateProperty gcs.spanner.output.database=<spanner database id> \
--templateProperty gcs.spanner.output.table=<spanner table id> \
--templateProperty gcs.spanner.output.saveMode=<Append|Overwrite|ErrorIfExists|Ignore> \
--templateProperty gcs.spanner.output.primaryKey=<column[(,column)*] - primary key columns needed when creating the table> \
--templateProperty gcs.spanner.output.batchInsertSize=<optional integer>
```
<br/><b>Postgresql dialect example</b>
<br/>Note: Currently we are supporting `Append` mode only. Tables must be created before running postgres dialect.
```
GCP_PROJECT=<gcp-project-id> \
REGION=<region>  \
GCS_STAGING_LOCATION=<gcs-staging-bucket-folder> \
bin/start.sh \
-- --template GCSTOSPANNER \
--templateProperty project.id=<gcp-project-id> \
--templateProperty gcs.spanner.input.format=<avro | parquet | orc | csv> \
--templateProperty gcs.spanner.input.location=<gcs path> \
--templateProperty gcs.spanner.output.instance=<spanner instance id> \
--templateProperty gcs.spanner.output.database=<spanner database id> \
--templateProperty gcs.spanner.output.table=<spanner table id> \
--templateProperty gcs.spanner.output.saveMode=<Append> \
--templateProperty gcs.spanner.output.primaryKey=<column[(,column)*] - primary key columns needed when creating the table> \
--templateProperty gcs.spanner.output.batchInsertSize=<optional integer> \
--templateProperty spanner.jdbc.dialect=postgresql
```

Note :- While running GCS to Spanner template with CSV file formats, header should be specified in CSV file and the Spark inferred data types should be in alignment with the data types of Spanner Tables. Otherwise, the job would fail.
As for other file formats all this information is by default being covered in their respective file formats like parquet / orc / avro.

## 4. Cloud Storage to JDBC

```
Please download the JDBC Driver of respective database and copy it to GCS bucket location.

export JARS=<GCS location for JDBC connector jar>

GCP_PROJECT=<gcp-project-id> \
REGION=<region>  \
GCS_STAGING_LOCATION=<gcs-staging-bucket-folder> \
bin/start.sh \
-- --template GCSTOJDBC \
--templateProperty project.id=<gcp-project-id> \
--templateProperty gcs.jdbc.input.format=<avro | csv | parquet | orc> \
--templateProperty gcs.jdbc.input.location=<gcs path> \
--templateProperty gcs.jdbc.output.driver=<jdbc driver required in single quotes> \
--templateProperty gcs.jdbc.output.url=<jdbc url along with username and password in single quotes> \
--templateProperty gcs.jdbc.output.table=<jdbc connection table id> \
--templateProperty gcs.jdbc.output.saveMode=<Append|Overwrite|ErrorIfExists|Ignore>

optional parameters:
--templateProperty gcs.jdbc.spark.partitions=<>
--templateProperty gcs.jdbc.output.batchInsertSize=<>

Specifying spark partitions is recommeneded when we have small number of current partitions. By default it will keep the initial partitions set by spark read() which will depend on the block size and number of source files. CAUTION: If you specify a higher number than the current number of partitions, spark will use repartition() for a complete reshuffle, which would add up extra time to your job run.

For the batchInsertSize, a high number is recommended for faster upload to RDBMS in case of bulk loads. By default it is 1000.

When the JDBC target is PostgreSQL it is recommended to include the connection parameter reWriteBatchedInserts=true in the JDBC URL to provide a significant performance improvement over the default setting.


Example execution:-

bin/start.sh \
-- --template GCSTOJDBC \
--templateProperty project.id=my-gcp-project \
--templateProperty gcs.jdbc.input.location=gs://my-gcp-project-bucket/empavro \
--templateProperty gcs.jdbc.input.format=avro \
--templateProperty gcs.jdbc.output.table=avrodemo \
--templateProperty gcs.jdbc.output.saveMode=Overwrite \
--templateProperty gcs.jdbc.output.url='jdbc:mysql://192.168.16.3:3306/test?user=root&password=root' \
--templateProperty gcs.jdbc.output.driver='com.mysql.jdbc.Driver' \
--templateProperty gcs.jdbc.spark.partitions=200 \
--templateProperty gcs.jdbc.output.batchInsertSize=100000 \

```

## 5. Cloud Storage to Cloud Storage

```
GCP_PROJECT=<gcp-project-id> \
REGION=<region>  \
GCS_STAGING_LOCATION=<gcs-staging-bucket-folder> \
bin/start.sh \
-- --template GCSTOGCS \
--templateProperty project.id=<gcp-project-id> \
--templateProperty gcs.gcs.input.format=<avro | parquet | orc> \
--templateProperty gcs.gcs.input.location=<gcs path> \
--templateProperty gcs.gcs.output.format=<avro | parquet | orc> \
--templateProperty gcs.gcs.output.location=<gcs path> \
--templateProperty gcs.gcs.write.mode=<Append|Overwrite|ErrorIfExists|Ignore>
--templateProperty gcs.gcs.temp.table=<temporary table name for data processing> \
--templateProperty gcs.gcs.temp.query=<sql query to process data from temporary table>

Example execution:-

bin/start.sh \
-- --template GCSTOJDBC \
--templateProperty project.id=my-gcp-project \
--templateProperty gcs.gcs.input.location=gs://my-gcp-project-input-bucket/filename.avro \
--templateProperty gcs.gcs.input.format=avro \
--templateProperty gcs.gcs.output.location=gs://my-gcp-project-output-bucket \
--templateProperty gcs.gcs.output.format=csv \
--templateProperty gcs.jdbc.output.saveMode=Overwrite
--templateProperty gcs.gcs.temp.table=tempTable \
--templateProperty gcs.gcs.temp.query='select * from global_temp.tempTable where sal>1500'

```

## 6. Cloud Storage To Mongo:

Download the following MongoDb connectors and copy it to Cloud Storage bucket location:
* [MongoDb Spark Connector](https://mvnrepository.com/artifact/org.mongodb.spark/mongo-spark-connector)
* [MongoDb Java Driver](https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver)

```
export GCP_PROJECT=<project-id>\
export REGION=<region>\
export SUBNET=<subnet>\
export GCS_STAGING_LOCATION=<gcs-staging-location-folder>\
export JARS=<gcs-location-to-mongodb-drivers>\

bin/start.sh \
-- --template GCSTOMONGO \
--templateProperty log.level="ERROR" \
--templateProperty gcs.mongodb.input.format=<csv|avro|parquet|json> \
--templateProperty gcs.mongodb.input.location=<gcs-input-location> \
--templateProperty gcs.mongodb.output.uri=<mongodb-output-uri> \
--templateProperty gcs.mongodb.output.database=<database-name>\
--templateProperty gcs.mongodb.output.collection=<collection-name> \
--templateProperty gcs.mongo.output.mode=<Append|Overwrite|ErrorIfExists|Ignore>

```
Example execution:
```
export GCP_PROJECT=mygcpproject
export REGION=us-west1
export SUBNET=projects/mygcpproject/regions/us-west1/subnetworks/test-subnet1
export GCS_STAGING_LOCATION="gs://dataproctemplatesbucket"
export JARS="gs://dataproctemplatesbucket/mongo_dependencies/mongo-java-driver-3.9.1.jar,gs://dataproctemplatesbucket/mongo_jar/mongo-spark-connector_2.12-2.4.0.jar"

bin/start.sh \
-- --template GCSTOMONGO \
--templateProperty log.level="ERROR" \
--templateProperty gcs.mongodb.input.format=avro \
--templateProperty gcs.mongodb.input.location="gs://dataproctemplatesbucket/empavro" \
--templateProperty gcs.mongodb.output.uri="mongodb://1.2.3.4:27017" \
--templateProperty gcs.mongodb.output.database="demo" \
--templateProperty gcs.mongodb.output.collection="test" \
--templateProperty gcs.mongo.output.mode="overwrite"
```

## 7. Deltalake To Iceberg

`deltalake.version.as_of` is an optional parameter which is default set to `0` means we will pick up the latest change only. We are providing below example to show how you can pass the value if you require time travel based on version number.

`iceberg.table.merge.schema` is an optional parameter which is default set to `false` means we will not merge schema while writing Iceberg table. Please set it `true` if you would like to merge the schema.

Hive Metastore is required for Iceberg tables. Please refer to our public [documentation](https://cloud.google.com/dataproc-metastore/docs/overview) to learn more about Dataproc Metastore.

General Execution For Version Based Time Travel of Deltalake Table:

```shell
export REGION=<REGION>
export GCP_PROJECT=<GCP_PROJECT>
export SUBNET=<SUBNET>
export METASTORE_SERVICE=<DATAPROC_METASTORE_SERVICE>

bin/start.sh -- --template GCSDELTALAKETOICEBERG \
 --templateProperty project.id=$GCP_PROJECT \
 --templateProperty deltalake.input.location="gs://dp-template-deltalake-iceberg/hive_warehouse/delta-table-sample" \
 --templateProperty deltalake.version.as_of=0 \
 --templateProperty iceberg.table.name="spark_catalog.default.dp_iceberg_tbl" \
 --templateProperty iceberg.gcs.output.mode="<Append|Overwrite>"
```

General Execution For Timestamp Based Time Travel of Deltalake Table:

```shell
export REGION=<REGION>
export GCP_PROJECT=<GCP_PROJECT>
export SUBNET=<SUBNET>
export METASTORE_SERVICE=<DATAPROC_METASTORE_SERVICE>

bin/start.sh -- --template GCSDELTALAKETOICEBERG \
 --templateProperty project.id=$GCP_PROJECT \
 --templateProperty gcsdeltalaketoiceberg.input.path="gs://dp-template-deltalake-iceberg/hive_warehouse/delta-table-sample" \
 --templateProperty deltalake.timestamp.as_of="2025-07-01" \
 --templateProperty iceberg.table.name="spark_catalog.default.dp_iceberg_tbl" \
 --templateProperty iceberg.gcs.output.mode="<Append|Overwrite>"
```

General Execution For Version Based Time Travel of Deltalake Table With Partition Columns:

```shell
export REGION=<REGION>
export GCP_PROJECT=<GCP_PROJECT>
export SUBNET=<SUBNET>
export METASTORE_SERVICE=<DATAPROC_METASTORE_SERVICE>

bin/start.sh -- --template GCSDELTALAKETOICEBERG \
 --templateProperty project.id=$GCP_PROJECT \
 --templateProperty gcsdeltalaketoiceberg.input.path="gs://dp-template-deltalake-iceberg/hive_warehouse/delta-table-sample" \
 --templateProperty deltalake.version.as_of=0 \
 --templateProperty iceberg.table.name="spark_catalog.default.dp_iceberg_tbl" \
 --templateProperty iceberg.table.partition.columns="col1,col2,col3" \
 --templateProperty iceberg.gcs.output.mode="<Append|Overwrite>"
```

Currently, our template is supporting moving data from deltalake to iceberg in append/overwrite mode. We are not supporting any UPDATE operation for now.