package io.github.alvinzhang86.activemq.topic.virtual;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.*;

/**
 * Created by zhangshuang on 16/3/28.
 */
public class Consumer {
    private static final String BROKER_URL = "tcp://101.200.180.127:61616";

    private static final Boolean NON_TRANSACTED = false;

    private static final long TIMEOUT = 200000;

    public static class ConsumerThread implements Runnable {

        private MessageConsumer consumer;

        private String consumerName;

        public ConsumerThread(MessageConsumer consumer, String consumerName) {
            this.consumer = consumer;
            this.consumerName = consumerName;
        }

        @Override
        public void run() {
            try {
                int i = 0;
                while (true) {
                    Message message = null;
                    message = this.consumer.receive(TIMEOUT);
                    if (message != null) {
                        if (message instanceof TextMessage) {
                            String text = ((TextMessage) message).getText();
                            System.out.println(this.consumerName + " Got Message #" + i++ + ". message: " + text);
                        }
                    } else {
                        break;
                    }
                }
                this.consumer.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }
    }


    public static void main(String[] args) {
        String url = BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        System.out.println("waiting... timeout after(s) " + TIMEOUT / 1000);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);

        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);

            Destination destinationA = session.createQueue("Consumer.A.VirtualTopic.Test-Topic");
            Destination destinationB = session.createQueue("Consumer.B.VirtualTopic.Test-Topic");

            MessageConsumer consumerA_1 = session.createConsumer(destinationA);
            Thread tA1 = new Thread(new ConsumerThread(consumerA_1, "Consumer_A_1"));
            tA1.start();

            MessageConsumer consumerA_2 = session.createConsumer(destinationA);
            Thread tA2 = new Thread(new ConsumerThread(consumerA_2, "Consumer_A_2"));
            tA2.start();


            MessageConsumer consumerB_1 = session.createConsumer(destinationB);
            Thread tB1 = new Thread(new ConsumerThread(consumerB_1, "Consumer_B_1"));
            tB1.start();

            MessageConsumer consumerB_2 = session.createConsumer(destinationB);
            Thread tB2 = new Thread(new ConsumerThread(consumerB_2, "Consumer_B_2"));
            tB2.start();

            while (tA1.isAlive() || tA2.isAlive() || tB1.isAlive() || tB2.isAlive()) {
                Thread.sleep(2000);
            }

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
}
