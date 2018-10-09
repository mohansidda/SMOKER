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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Properties;

public class SmokerProducer {
    private static final Logger log = LogManager.getLogger(SmokerProducer.class);
    SmokerProducerConfig config;
    KafkaProducer producer;
    Properties props;

    public SmokerProducer() {
        this.config = SmokerProducerConfig.fromEnv();
        this.props = SmokerProducerConfig.createProperties(config);
        this.producer = new KafkaProducer(props);
    }

    public void sendMessage(String message) throws InterruptedException {
        log.info("Sending message.");
        producer.send(new ProducerRecord(config.getTopic(),  message));
        log.info("Message sent.");
    }

    public void closeProducer() {
        producer.close();
    }
}
