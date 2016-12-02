# 内容说明

# 本目录下

Producer.java 是一个Queue的生产者
Consumer.java 是一个Queue的消费者
ExclusiveConsumer.java 是一个Queue的独占消费者

# 独占消费（ExclusiveConsumer）的一些情况
 
 1. 原有Producer 和 Consumer 在线，
    当ExclusiveConsumer-1上线后，其他的consumer 全部接收不到消息。
    当ExclusiveConsumer-2上线后，ExclusiveConsumer-2收不到消息， ExclusiveConsumer-1持续收到消息。其他的consumer 全部接收不到消息。
 
 2. 多个ExclusiveConsumer同时在线
    当Producer上线后， 其中一个ExclusiveConsumer会持续收到消息，其他ExclusiveConsumer收不到消息。

# 监控队列，不消费消息(Browser)

 1. Browser 可以查看当前队列Queue中的数据，但不消费数据，只是浏览。内容不仅限于消息体内容，还包含消息的一些属性。
 
 
 
