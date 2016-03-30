package io.github.alvinzhang86.activemq.temp.destinations;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by zhangshuang on 16/3/25.
 */
public class Consumer {
    private static final String BROKER_URL = "tcp://101.200.180.127:61616";
    private static final Boolean NON_TRANSACTED = false;
    private static final long TIMEOUT = 20000;

    public static void main(String[] args) {

        String url = BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        System.out.println("\nWaiting to receive messages... will timeout after " + TIMEOUT / 1000 +"s");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            final Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("test-queue");
            MessageConsumer consumer = session.createConsumer(destination);

            int i = 0;
            while (true) {
                Message message = consumer.receive(TIMEOUT);

                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + i++ + ". message: " + text);
                        Destination replyTo = message.getJMSReplyTo();  // 获取消息中附加的应答队列
                        MessageProducer producer = session.createProducer(replyTo); // 为应答创建应答消息生产者
                        producer.send(session.createTextMessage("You made it to the consumer, here is your response")); // 发送应答
                        producer.close();
                    }
                } else {
                    break;
                }
            }

            consumer.close();
            session.close();

        } catch (Exception e) {
            System.out.println("Caught exception!");
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    System.out.println("Could not close an open connection...");
                }
            }
        }
    }
}
