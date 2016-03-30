package io.github.alvinzhang86.activemq.topic;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangshuang on 16/3/23.
 */
public class Subscriber implements MessageListener {

    private static final String BROKER_URL = "tcp://101.200.180.127:61616"; // mq地址
    private static final Boolean NON_TRANSACTED = false;

    private static final Boolean DURABLE = false; // 是否是持久订阅
    private static final String CLIENT_ID = "test-durable-client";// 持久订阅下有效，持久订阅下，mq server 需要记录的

    private final CountDownLatch countDownLatch;

    public Subscriber(CountDownLatch latch) {
        this.countDownLatch = latch;
    }

    public static void main(String[] args) {
        String url = BROKER_URL;
        if (args.length > 0) {
            url = args[0];
        }

        System.out.println("waiting... ");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);
        Connection connection = null;
        final CountDownLatch latch = new CountDownLatch(1);

        try {
            connection = connectionFactory.createConnection();

            if (DURABLE) { // 持久订阅
                connection.setClientID(CLIENT_ID);
            }

            connection.start();
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = null;

            if (DURABLE) { // 持久订阅
                Topic destination = session.createTopic("test-topic");
                consumer = session.createDurableSubscriber(destination, CLIENT_ID);
            } else { // 非持久订阅
                Destination destination = session.createTopic("test-topic");
                consumer = session.createConsumer(destination);
            }

            consumer.setMessageListener(new Subscriber(latch));
            latch.await();
            consumer.close();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                if ("END".equalsIgnoreCase(text)) {
                    System.out.println("Recieve end message");
                    countDownLatch.countDown();
                } else {
                    System.out.println("Recieve message:" + text);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
