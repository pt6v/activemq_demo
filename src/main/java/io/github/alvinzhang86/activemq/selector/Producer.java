package io.github.alvinzhang86.activemq.selector;

import io.github.alvinzhang86.activemq.Config;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by zhangshuang on 16/3/25.
 */
public class Producer {

    private static final Boolean NON_TRANSACTED = false;
    private static final int NUM_MESSAGES_TO_SEND = 100;
    private static final long DELAY = 1000;
    private static final Boolean TOPIC = true;

    public static void main(String[] args) {
        String url = Config.BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PASSWORD, url);
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = null;
            if (TOPIC) {
                destination = session.createTopic("test-topic");
            } else {
                destination = session.createQueue("test-queue");
            }

            MessageProducer producer = session.createProducer(destination);

            for (int i = 0; i < NUM_MESSAGES_TO_SEND; i++) {
                TextMessage message = session.createTextMessage("Message #" + i);
                System.out.println("Sending message #" + i);
                if (i % 2 == 0) {
                    System.out.println("Sending to me");
                    message.setStringProperty("intended", "me");
                } else {
                    System.out.println("Sending to you");
                    message.setStringProperty("intended", "you");
                }
                producer.send(message);
                Thread.sleep(DELAY);
            }

            producer.close();
            session.close();

        } catch (Exception e) {
            System.out.println("Caught exception!");
        } finally {
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
