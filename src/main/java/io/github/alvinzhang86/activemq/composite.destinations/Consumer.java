package io.github.alvinzhang86.activemq.composite.destinations;

import io.github.alvinzhang86.activemq.Config;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by zhangshuang on 16/3/25.
 */
public class Consumer {
    private static final Boolean NON_TRANSACTED = false;
    private static final long TIMEOUT = 20000;

    public static void main(String[] args) {
        String url = Config.BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        System.out.println("\nWaiting to receive messages... will timeout after " + TIMEOUT / 1000 +"s");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PASSWORD, url);
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("test-queue");
            Destination destinationFoo = session.createQueue("test-queue-foo");
            Destination destinationBar = session.createQueue("test-queue-bar");
            Destination destinationTopicFoo = session.createTopic("test-topic-foo");

            MessageConsumer consumer = session.createConsumer(destination);
            MessageConsumer consumerFoo = session.createConsumer(destinationFoo);
            MessageConsumer consumerBar = session.createConsumer(destinationBar);
            MessageConsumer consumerTopicFoo = session.createConsumer(destinationTopicFoo);

            int i = 0;
            // todo 一个队列的阻塞会引起其他队列收不到消息
            while (true) {
                Message message = consumer.receive(TIMEOUT);

                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + i++ + ". message on test-queue: " + text);
                    }
                } else {
                    break;
                }

                message = consumerFoo.receive(TIMEOUT);

                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + i++ + ". message on test-queue-foo: " + text);
                    }
                } else {
                    break;
                }

                message = consumerBar.receive(TIMEOUT);

                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + i++ + ". message on test-queue-bar: " + text);
                    }
                } else {
                    break;
                }

                message = consumerTopicFoo.receive(TIMEOUT);

                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Got " + i++ + ". message on test-topic-bar: " + text);
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
