package io.github.alvinzhang86.activemq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by zhangshuang on 16/3/23.
 */
public class Publisher {

    private static final String BROKER_URL = "tcp://101.200.180.127:61616";

    private static final Boolean NON_TRANSACTED = false;

    private static final int NUM_MESSAGE_TO_SEND = 100;

    private static final long DELAY = 100;

    public static void main(String[] args) {
        String url = BROKER_URL;
        if (args.length > 0) {
            url = args[0];
        }
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("test-topic");
            MessageProducer producer = session.createProducer(destination);
            for( int i = 0 ; i < NUM_MESSAGE_TO_SEND; i++) {
                TextMessage message = session.createTextMessage("Message #" + i);
                System.out.println("Sending Message #" + i);
                producer.send(message);
                Thread.sleep(DELAY);
            }
            producer.send(session.createTextMessage("END"));

            producer.close();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
