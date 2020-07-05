import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class Publisher {
    private static final String EXCHANGE_NAME = "job_service";
    private static final String TOPIC_NAME = "message.sent";
    private ConnectionFactory factory;
    private final int toSendConst = 5000;
    private long startTime;
    private final long dataInterval = 0;
    private int idCounting = 1;
    private String job[] = new String[]{"Programmer", "Volunteer", "Technical Writer", "Lawyer", "Librarian"};

    Publisher(String[] args) {
        factory = new ConnectionFactory();
        factory.setHost(args[0]);
        factory.setUsername(args[1]);
        factory.setPassword(args[2]);
    }

    private int randomBetween(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public String generateData() {
        return String.format("{\"id\": %d, \"job\": %s, \"age\": %s, \"salary\": %s}", idCounting++, job[randomBetween(0, job.length - 1)], randomBetween(18, 67), randomBetween(1500, 6000));
    }

    public void start() throws IOException, InterruptedException, TimeoutException {
        ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        ConfirmCallback cleanOutstandingConfirms = (sequenceNumber, multiple) -> {
            if(multiple) {
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(
                        sequenceNumber, true
                );
                confirmed.clear();
            } else {
                outstandingConfirms.remove(sequenceNumber);
            }
        };

        try(Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            System.out.println(channel.isOpen());
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            channel.confirmSelect();
            System.out.println(channel.isOpen());
            channel.addConfirmListener(cleanOutstandingConfirms, (sequenceNumber, multiple) -> {
                String body = outstandingConfirms.get(sequenceNumber);
                System.err.format(
                        "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                        body, sequenceNumber, multiple
                );
                cleanOutstandingConfirms.handle(sequenceNumber, multiple);
            });

            startTime = System.currentTimeMillis();
            System.out.println(channel.isOpen());

            for(int i = 0; i < toSendConst; ++i) {
                channel.basicPublish(EXCHANGE_NAME, TOPIC_NAME, null, generateData().getBytes(StandardCharsets.UTF_8));
                try {
                    Thread.sleep(dataInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ToSend: " + (toSendConst - i));
            }

            if(!waitUntil(Duration.ofSeconds(60), outstandingConfirms::isEmpty)) {
                throw new IllegalStateException("All messages could not be confirmed in 60 seconds");
            }

            System.out.println("Time needed: " + ((System.currentTimeMillis() - startTime) / 1000));
        }
    }

    private boolean waitUntil(Duration timeout, BooleanSupplier condition) throws InterruptedException {
        int waited = 0;
        while(!condition.getAsBoolean() && waited < timeout.toMillis()) {
            Thread.sleep(100L);
            waited = +100;
        }
        return condition.getAsBoolean();
    }
}
