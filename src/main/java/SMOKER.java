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

import org.apache.kafka.common.requests.AlterReplicaLogDirsRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class SMOKER {
    private JTextField textField1;
    private JButton sendButton;
    private JPanel panel;
    private JTextArea textArea1;
    SmokerProducer producer = new SmokerProducer();
    static SmokerConsumer consumer = new SmokerConsumer();
    boolean me = false;
    boolean you = false;
    private static final Logger log = LogManager.getLogger(SMOKER.class);
    private static ArrayList<String> functions = new ArrayList();



    public SMOKER() {
        log.info(
                   "\n  ██████  ███▄ ▄███▓ ▒█████   ██ ▄█▀▓█████  ██▀███  \n" +
                        "▒██    ▒ ▓██▒▀█▀ ██▒▒██▒  ██▒ ██▄█▒ ▓█   ▀ ▓██ ▒ ██▒\n" +
                        "░ ▓██▄   ▓██    ▓██░▒██░  ██▒▓███▄░ ▒███   ▓██ ░▄█ ▒\n" +
                        "  ▒   ██▒▒██    ▒██ ▒██   ██░▓██ █▄ ▒▓█  ▄ ▒██▀▀█▄  \n" +
                        "▒██████▒▒▒██▒   ░██▒░ ████▓▒░▒██▒ █▄░▒████▒░██▓ ▒██▒\n" +
                        "▒ ▒▓▒ ▒ ░░ ▒░   ░  ░░ ▒░▒░▒░ ▒ ▒▒ ▓▒░░ ▒░ ░░ ▒▓ ░▒▓░\n" +
                        "░ ░▒  ░ ░░  ░      ░  ░ ▒ ▒░ ░ ░▒ ▒░ ░ ░  ░  ░▒ ░ ▒░\n" +
                        "░  ░  ░  ░      ░   ░ ░ ░ ▒  ░ ░░ ░    ░     ░░   ░ \n" +
                        "      ░         ░       ░ ░  ░  ░      ░  ░   ░    "
        );
        log.info("Strimzi - Messaging - Openshift - Kafka - Exposing - Redhat");
        functions.add("!temperature");
        functions.add("!time");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendMessage(null);
            }
        });

        Thread consumerThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String received = consumer.consume();
                        if (received.length() > 0) {
                            me = false;
                            log.debug("Received! '" + received + "'");
                            if (received.equals("!time\n")) {
                                Date date = new Date();
                                sendMessage("TimeBot says: It is\n\t\t\t" + date.toString());
                            } else if (received.equals("!temperature\n")) {
                                Random rn = new Random();
                                for (int i = 0; i < 10; i++) {
                                    sendMessage(Integer.toString(rn.nextInt((25 - 18) + 1) + 18) + " °C");
                                    Thread.sleep(1000);
                                }
                            }
                            else
                            {
                                if (!you) {
                                    textArea1.append("\t\t\t" + consumer.config.getReceiver() + ":\n");
                                    you = true;
                                }
                                textArea1.append("\t\t\t" + received);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        };

        Thread close = new Thread() {
            @Override
            public void run() {
                consumerThread.stop();
                producer.closeProducer();
                consumer.closeConsumer();
            }
        };

        consumerThread.start();
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(null);
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(close);
    }

    public void sendMessage(String msg) {
        try {
            log.debug("Sending!" + msg);
            if (msg != null) {
                producer.sendMessage(msg);
            } else {
                String text = textField1.getText();
                if (text.length() > 0 && !functions.contains(text)) {
                    you = false;
                    if (!me) {
                        textArea1.append(producer.config.getSender() + ":\n");
                        me = true;
                    }
                    textArea1.append(text + "\n");
                    producer.sendMessage(text);
                    textField1.setText("");
                } else {
                    producer.sendMessage(text);
                    textField1.setText("");
                }
            }
            } catch(InterruptedException e){
                e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SMOKER");
        frame.setContentPane(new SMOKER().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
