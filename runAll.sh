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
	oc apply -f /home/sknot/RH/strimzi/examples/install/cluster-operator

	#deploy cluster
	oc apply -f kafka-ephemeral.yaml

	#create users
	oc apply -f expoDemo1/kafka-user.yaml
	oc apply -f expoDemo2/kafka-user.yaml

if [ "$answer" = "yes" ]; then
	#wait for kafka cluster boot up
	sleep 120
fi
#run clients
cd expoDemo1
. getAuthAndStart.sh &
cd ..
cd expoDemo2
. getAuthAndStart.sh &
cd ..
