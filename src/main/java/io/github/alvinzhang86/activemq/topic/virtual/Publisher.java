package io.github.alvinzhang86.activemq.topic.virtual;

import io.github.alvinzhang86.activemq.Config;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by zhangshuang on 16/3/28.
 */
public class Publisher {

    private static final Boolean NON_TRANSACTED = false;

    private static final int NUM_MESSAGE_TO_SEND = 100;

    private static final long DELAY = 200;

    public static void main(String[] args) {
        String url = Config.BROKER_URL;
        if (args.length > 0) {
            url = args[0];
        }
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PASSWORD, url);
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("VirtualTopic.Test-Topic"); // 创建一个虚拟topic

            MessageProducer producer = session.createProducer(topic);
//            producer.setDeliveryMode(DeliveryMode.PERSISTENT); // 发送消息时使用持久模式
            for (int i = 0; i < NUM_MESSAGE_TO_SEND; i++) {
                TextMessage message = session.createTextMessage("VirtualTopic Message #" + i);
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
