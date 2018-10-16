#!/usr/bin/env bash

#clean-up
rm truststore.jks user.p12

oc extract secret/my-user-2 --keys=user.crt --to=- > user.crt
oc extract secret/my-user-2 --keys=user.key --to=- > user.key
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- > ca.crt

echo "yes" | keytool -import -trustcacerts -file ca.crt -keystore truststore.jks -storepass 123456 -alias `(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)`
openssl pkcs12 -export -in user.crt -inkey user.key -name my-user-2 -password pass:123456 -out user.p12
rm user.crt user.key ca.crt

#echo -n "Bootstrap: "
bootstrap=`oc get service my-cluster-kafka-external-bootstrap -o=jsonpath='{.status.loadBalancer.ingress[0].ip}{"\n"}'`
bootstrap="${bootstrap}:9094"

export SMOKER_BOOTSTRAP_SERVERS=${bootstrap}
export SMOKER_TRUSTSTORE_PASSWORD="123456"
export SMOKER_TRUSTSTORE_PATH="/home/sknot/RH/SMOKER/expoDemoUser2/truststore.jks"
export SMOKER_KEYSTORE_PASSWORD="123456"
export SMOKER_KEYSTORE_PATH="/home/sknot/RH/SMOKER/expoDemoUser2/user.p12"
export SMOKER_TOPIC_USR1USR2="smoker-topic-usr2-2-usr1"
export SMOKER_TOPIC_USR2USR1="smoker-topic-usr1-2-usr2"
export SMOKER_GROUP_ID="my-group-2"
export SMOKER_RECEIVER="user1"
export SMOKER_SENDER="user2"

java -jar ../target/smoker-1.0-SNAPSHOT.jar -gui
