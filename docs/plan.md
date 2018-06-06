# Next Step

1. Support partition topics

   1. PartitionedProducer

2. Support consumer : both client and server side

3. Support offset management

   1. DlogOffsetStorage: Use distributed log to store offset ,but use a different namespace and stream

      name with client, and figure out how to create a namespace.

4. Finish 3 rpc call : FetchOffset、ConsumerSubscribe、PullMessage

5. Improve log system

6. admin api : create a topic 

7. broker state management ,such as which broker owns the topoc (maybe store this metadata in zookeeper).


