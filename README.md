# activemq_demo

## 平台搭建

## 应用示例

[项目代码地址](https://github.com/AlvinZhang86/activemq_demo)

### Queue方式

#### 示意图

![queue:一对一的发送方式](https://raw.githubusercontent.com/AlvinZhang86/image_web/master/activemq/queue.png)

#### 示例代码

    io.github.alvinzhang86.activemq.queue.Producer.java
    io.github.alvinzhang86.activemq.queue.Consumer.java

#### 备份消费和独占(exlutive)消费

区别:普通的消费者的消费消息的方式称之为备份消费，独占消费主要是针对备份消费产生的概念。

假设一个队列被多个消费者消费的情况下，如果没有独占消费，那么一条消息被哪个消费者消费的行为是随机的。

如果存在一个独占消费者，那么这个消息队列的消息会被该独占消费这独占。

如果存在多个活跃的独占消费者，那么随机的一个独占消费者将消费队列中的所有消息。

如果存在多个的独占消费者，但全部为关闭状态，随机的一个备份消费者将消费队列中的所有消息。

    io.github.alvinzhang86.activemq.queue.ExclusiveConsumer.java


#### 不接收队列中的消息的情况下，查看队列中的消息信息(监控)

    io.github.alvinzhang86.activemq.queue.Browser.java

### Topic方式

#### 示意图

![topic:一对多的发送方式](https://raw.githubusercontent.com/AlvinZhang86/image_web/master/activemq/topic.png)

#### 示例代码

    io.github.alvinzhang86.activemq.topic.Publisher.java
    io.github.alvinzhang86.activemq.topic.Subscriber.java

#### 持久订阅和非持久订阅

持久订阅与非持久订阅主要值得是消息的订阅方（subscribe）

对于非持久订阅：消息发布者在订阅者离线情况下，发布的任何消息，消息订阅者即使以后上线了，也无法接收到已经失去的消息。

对于持久订阅：消息发布者在订阅者离线的情况下，发布的消息，当消息订阅者上线后，可以接收到离线时没有收到的消息。

### 其他说明

#### 向多个消息中间件中发送同一条消息

Producer 在定义 中间件时 使用定义方式：

    Destination destination = session.createQueue("test-queue,test-queue-foo,test-queue-bar,topic://test-topic-foo");// 定义了三个Queue一个Topic


#### 只获取部分特定的消息

    io.github.alvinzhang86.activemq.selector.Producer.java
    io.github.alvinzhang86.activemq.selector.Consumer.java

#### ActiveMQ对事务的支持

    io.github.alvinzhang86.activemq.transaction.Client.java

#### 请求/应答机制的实现

    io.github.alvinzhang86.activemq.temp.destinations.ProducerRequestReply.java
    io.github.alvinzhang86.activemq.temp.destinations.Consumer.java

#### QUEUE/TOPIC的通配符

    io.github.alvinzhang86.activemq.wildcard.Client.java

#### 虚拟Topic

##### Topic 面对的问题：

ActiveMQ中，topic只有在持久订阅（durable subscribe）下是持久化的。这种情况下存在两个问题：

同一应用内订阅端负载均衡的问题：同一个应用上的一个持久订阅不能使用多个subscriber来共同承担消息处理功能。因为每个都会获取所有消息。queue模式可以解决这个问题，Producer端又不能将消息发送到多个Consumer端。所以，既要发布订阅，又要让消费者分组，这个功能jms规范本身是没有的。

同一应用内订阅端failover的问题：由于只能使用单个的持久订阅者，如果这个订阅者出错，则应用就无法处理消息了，系统的健壮性不高。

##### 虚拟Topic示意图

![虚拟Topic](https://raw.githubusercontent.com/AlvinZhang86/image_web/master/activemq/ActiveMQ-VirtualTopic.jpg)

##### 虚拟Topic建立

对于Publisher来说，消息是一个正常的Topic， 但Topic的名称 必须为

    "VirtualTopic."+<Topic名称>

对于Consumer来说，消息是一个队列队里名设置为

    "Consumer." + <队列标识> + ".VirtualTopic." + <Topic名称>

##### 代码位置

    io.github.alvinzhang86.activemq.topic.virtual.Publisher.java
    io.github.alvinzhang86.activemq.topic.virtual.Comsumer.java
