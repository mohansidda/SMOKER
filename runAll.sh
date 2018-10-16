#!/usr/bin/env bash


echo "Restart cluster? [yes|no]"
read answer

#rebuild SMOKER client
mvn clean package install

#(re)start cluster
if [ "$answer" = "yes" ]; then
	oc cluster down
	oc cluster up
	oc login -u system:admin
fi

	#install resources
	oc apply -f /home/sknot/RH/strimzi/install/cluster-operator

	#deploy cluster
	oc apply -f kafka-ephemeral.yaml

	#create topics
	oc apply -f kafka-topic-1.yaml
	oc apply -f kafka-topic-2.yaml

	#create users
	oc apply -f expoDemoUser1/kafka-user.yaml
	oc apply -f expoDemoUser2/kafka-user.yaml

	sleep 30
if [ "$answer" = "yes" ]; then
	#wait for kafka cluster boot up
	sleep 120
fi
#run clients
cd expoDemoUser1
. getAuthAndStart.sh &
cd ..
cd expoDemoUser2
. getAuthAndStart.sh &
cd ..
