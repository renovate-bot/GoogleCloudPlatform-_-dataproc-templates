/*
 * Copyright (C) 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.dataproc.templates.databases;

import static com.google.cloud.dataproc.templates.util.TemplateConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.google.cloud.dataproc.templates.util.PropertyUtil;
import com.google.cloud.dataproc.templates.util.ValidationUtil;
import java.util.stream.Stream;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoToBQTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoToBQTest.class);
  private MongoToBQ mongoToBQ;

  @BeforeEach
  void setup() {
    SparkSession spark = SparkSession.builder().master("local").getOrCreate();
  }

  @ParameterizedTest
  @MethodSource("propertyKeys")
  void runTemplateWithValidParameters(String propKey) {

    LOGGER.info("Running test: runTemplateWithValidParameters");
    PropertyUtil.getProperties().setProperty(PROJECT_ID_PROP, "test");
    PropertyUtil.getProperties().setProperty(MONGO_BQ_INPUT_URI, "mongodb://10.0.0.57:27017");
    PropertyUtil.getProperties().setProperty(MONGO_BQ_INPUT_DATABASE, "demo");
    PropertyUtil.getProperties().setProperty(MONGO_BQ_INPUT_COLLECTION, "dummyusers");
    PropertyUtil.getProperties().setProperty(MONGO_BQ_OUTPUT_MODE, "Append");
    PropertyUtil.getProperties().setProperty(MONGO_BQ_OUTPUT_DATASET, "dataproc_templates");
    PropertyUtil.getProperties().setProperty(MONGO_BQ_OUTPUT_TABLE, "mongotobq");
    PropertyUtil.getProperties()
        .setProperty(MONGO_BQ_TEMP_BUCKET_NAME, "dataproc-templates/integration-testing/mongotobq");
    MongoToBQConfig mongoToBQConfig = MongoToBQConfig.fromProperties(PropertyUtil.getProperties());
    mongoToBQ = new MongoToBQ(mongoToBQConfig);
    assertDoesNotThrow(mongoToBQ::validateInput);
  }

  @ParameterizedTest
  @MethodSource("propertyKeys")
  void runTemplateWithInvalidParameters(String propKey) {

    LOGGER.info("Running test: runTemplateWithInvalidParameters");
    PropertyUtil.getProperties().setProperty(propKey, "");
    MongoToBQConfig mongoToBQConfig = MongoToBQConfig.fromProperties(PropertyUtil.getProperties());
    mongoToBQ = new MongoToBQ(mongoToBQConfig);

    ValidationUtil.ValidationException exception =
        assertThrows(ValidationUtil.ValidationException.class, mongoToBQ::validateInput);
  }

  static Stream<String> propertyKeys() {

    return Stream.of(
        MONGO_BQ_INPUT_URI,
        MONGO_BQ_INPUT_DATABASE,
        MONGO_BQ_INPUT_COLLECTION,
        MONGO_BQ_OUTPUT_MODE,
        MONGO_BQ_OUTPUT_DATASET,
        MONGO_BQ_OUTPUT_TABLE,
        MONGO_BQ_TEMP_BUCKET_NAME);
  }
}
