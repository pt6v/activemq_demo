package io.github.alvinzhang86.activemq.temp.destinations;

import io.github.alvinzhang86.activemq.Config;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.swing.text.DefaultEditorKit;
import javax.xml.soap.Text;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangshuang on 16/3/25.
 */
public class ProducerRequestReply {
    private static final Boolean NON_TRANSACTED = false;
    private static final int NUM_MESSAGES_TO_SEND = 100;
    private static final long DELAY = 1000;

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
            Destination destination = session.createQueue("test-queue");
            MessageProducer producer = session.createProducer(destination);

            // 创建监听应答的tempraryQueue
            Destination replyDest = session.createTemporaryQueue();
            // set up the consumer to handle the reply
            MessageConsumer replyConsumer = session.createConsumer(replyDest); // 创建应答队列的消费者
            replyConsumer.setMessageListener(new MessageListener() { // 监听应答
                @Override
                public void onMessage(Message message) {
                    System.out.println("*** REPLY *** ");
                    System.out.println(message.toString());
                }
            });

            for(int i = 0; i < NUM_MESSAGES_TO_SEND; i++) {
                TextMessage message = session.createTextMessage("Message #" + i +" and I need a response for this, please");

                System.out.println("Sending Message #" + i);
                message.setJMSReplyTo(replyDest); // 消息中增加通知告诉消费者将应带发送到前文创建的tempraryQueue中
                producer.send(message);
                Thread.sleep(DELAY);
            }

            TimeUnit.SECONDS.sleep(2);
            producer.close();
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
