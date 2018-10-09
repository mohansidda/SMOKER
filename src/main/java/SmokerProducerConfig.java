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

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class SmokerProducerConfig {
    private static final Logger log = LogManager.getLogger(SmokerProducerConfig.class);

    private final String bootstrapServers;
    private final String topic;
    private String acks = "1";
    private final String trustStorePassword;
    private final String trustStorePath;
    private final String keyStorePassword;
    private final String keyStorePath;
    private final String sender;

    public SmokerProducerConfig(String bootstrapServers, String topic, String trustStorePassword, String trustStorePath, String keyStorePassword, String keyStorePath, String sender) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.trustStorePassword = trustStorePassword;
        this.trustStorePath = trustStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyStorePath = keyStorePath;
        this.sender = sender;
    }

    public static SmokerProducerConfig fromEnv() {
        String bootstrapServers = System.getenv("SMOKER_BOOTSTRAP_SERVERS");
        String topic = System.getenv("SMOKER_TOPIC_USR1USR2");
        String sender = System.getenv("SMOKER_SENDER");
        String trustStorePassword = System.getenv("SMOKER_TRUSTSTORE_PASSWORD") == null ? null : System.getenv("SMOKER_TRUSTSTORE_PASSWORD");
        String trustStorePath = System.getenv("SMOKER_TRUSTSTORE_PATH") == null ? null : System.getenv("SMOKER_TRUSTSTORE_PATH");
        String keyStorePassword = System.getenv("SMOKER_KEYSTORE_PASSWORD") == null ? null : System.getenv("SMOKER_KEYSTORE_PASSWORD");
        String keyStorePath = System.getenv("SMOKER_KEYSTORE_PATH") == null ? null : System.getenv("SMOKER_KEYSTORE_PATH");

        return new SmokerProducerConfig(bootstrapServers, topic, trustStorePassword, trustStorePath, keyStorePassword, keyStorePath, sender);
    }

    public static Properties createProperties(SmokerProducerConfig config) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        if (config.getTrustStorePassword() != null && config.getTrustStorePath() != null)   {
            log.info("Configuring truststore");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.getTrustStorePassword());
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.getTrustStorePath());
        }

        if (config.getKeyStorePassword() != null && config.getKeyStorePath() != null)   {
            log.info("Configuring keystore");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.getKeyStorePassword());
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.getKeyStorePath());
        }
        return props;
    }
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public String getAcks() {
        return acks;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public String getSender() {
        return sender;
    }
}