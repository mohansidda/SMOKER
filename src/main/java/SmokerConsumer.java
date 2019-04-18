/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.Properties;

public class SmokerConsumer {
    private static final Logger log = LogManager.getLogger(SmokerConsumer.class);
    SmokerConsumerConfig config;
    KafkaConsumer consumer ;
    Properties props;
    boolean commit;

    public SmokerConsumer() {
        config = SmokerConsumerConfig.fromEnv();
        props = SmokerConsumerConfig.createProperties(config);
        consumer = new KafkaConsumer(props);
        commit = !Boolean.parseBoolean(config.getEnableAutoCommit());
        consumer.subscribe(Collections.singletonList(config.getTopic()));
    }

    public String consume() {
        String recieved = "";


        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            int i = 0;
            for (ConsumerRecord<String, String> record : records) {
                i++;
                if (i > 1)
                    recieved += "\t\t\t" + record.value() + "\n";
                else
                    recieved += record.value() + "\n";
                log.debug(record.key());

                if (commit) {
                    consumer.commitSync();
                }
            }
            return recieved;
        }
    }

    public void closeConsumer()
    {
        consumer.close();
    }}
