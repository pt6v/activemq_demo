package io.github.alvinzhang86.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangshuang on 16/3/25.
 */
public class Browser {
    private static final String BROKER_URL = "tcp://101.200.180.127:61616";
    private static final Boolean NON_TRANSACTED = false;
    private static final long DELAY = 100;

    public static void main(String[] args) {
        String url = BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Queue destination = session.createQueue("test-queue");
            QueueBrowser browser = session.createBrowser(destination);
            Enumeration enumeration = browser.getEnumeration();

            while (enumeration.hasMoreElements()) {
                TextMessage message = (TextMessage) enumeration.nextElement();
                System.out.println("Browsing: " + message);
                TimeUnit.MILLISECONDS.sleep(DELAY);
            }

            session.close();

        } catch (Exception e) {
            System.out.println("Caught exception!" + e.getMessage());
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
