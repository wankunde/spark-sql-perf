/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.databricks.spark.sql.perf.tpcds

import org.apache.spark.sql.SparkSession

object CreateTPCTables {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Create TPC Tables")
      .master("local")
      .enableHiveSupport()
      .getOrCreate()
    val sqlContext = spark.sqlContext

    val rootDir = "hdfs://localhost:9000/tpcds" // root directory of location to create data in.
    val databaseName = "tpcds_1g" // name of database to create.
    val scaleFactor = "1" // scaleFactor defines the size of the dataset to generate (in GB).
    val format = "parquet" // valid spark format like parquet "parquet".
    // Run:
    val tables = new TPCDSTables(sqlContext,
      dsdgenDir = "/Users/wakun/ws/apache/tpcds-kit/tools", // location of dsdgen
      scaleFactor = scaleFactor,
      useDoubleForDecimal = false, // true to replace DecimalType with DoubleType
      useStringForDate = false) // true to replace DateType with StringType

    tables.createExternalTables(rootDir, "parquet", databaseName, overwrite = true, discoverPartitions = true)
    tables.analyzeTables(databaseName, analyzeColumns = true)

    // For CBO only, gather statistics on all columns:
    // tables.analyzeTables(databaseName, analyzeColumns = true)
  }
}
