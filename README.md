# SMOKER
Strimzi - Messaging - Openshift - Kafka - Exposing - Redhat

This is an example client using exposed Kafka broker running in OpenShift. 
The client can send and receive messages from specified topics. 
The client can force another client to become "bot" by using special commands (for now `!temperature` and `!time`)
Clients are using two topics, see figure below.
```

           _________                             _________
        _ |topic1to2|                         _ |topic2to1|    
publish /| ^^^^^^^^^ \  subscribe             /| ^^^^^^^^^ \
 _____ /             _\| _____         _____ /             _\| _____   
|user1|                 |user2|       |user2|                 |user1| 
 ^^^^^                   ^^^^^         ^^^^^                   ^^^^^   
```

### Run
To run this example you need running OpenShift cluster. Let's list all steps.
* deploying OpenShift cluster - `oc cluster up` 
* login as admin - `oc login -u system:admin`
* install custom resources and cluster operator - `oc apply -f path/to/install`
* deploy example Kafka cluster - `oc apply -f kafka-ephemeral.yaml`
* create two topics (as menioned above) - `oc aplly -f kafka-topic1.yaml` and `oc aplly -f kafka-topic2.yaml`
* create two users `oc apply -f expoDemoUser1/kafka-user.yaml` and `oc apply -f expoDemoUser2/kafka-user.yaml`
* extract secrets and run clients
    * `cd expoDemoUserN` where N is in {1, 2}
    * this step consist by many minor steps. Please use `getAuthAndStart.sh` script in each `expoDemoUserN` folder. 
    Before running it you need to adjust paths in this script.
    
Note there is a bash script `runAll.sh` which does all these steps. 
But it is *not* recommended to use it because it does not guarantee correct order of the commands.
(For example creating Kafka may take few minutes and we are accessing its exposed bootstrap service, etc.)

### Usage
When the client is running you should see its simple GUI. There is text area displaying received/send messages, 
text edit making possible to type new message and a button which is supposed to send a message. Sending a message
can be also performed by hitting `enter` key on your keyboard. 