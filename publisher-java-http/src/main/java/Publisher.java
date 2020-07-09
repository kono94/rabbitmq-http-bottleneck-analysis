import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.concurrent.*;

public class Publisher {
    private URL[] servicesURLs;
    private ExecutorService executorService;
    private BlockingQueue<Runnable> workerQueue;
    private int received = 0;
    private final int toSendConst;
    private int toSend;
    private long startTime;
    private final long dataInterval;
    private int idCounting = 1;
    private String job[] = new String[]{"Programmer", "Volunteer", "Technical Writer", "Lawyer", "Librarian"};

    public Publisher(String[] serviceURLs) throws MalformedURLException {
        toSendConst = Integer.parseInt(System.getenv("MESSAGE_COUNT"));
        toSend = toSendConst;
        dataInterval = Integer.parseInt(System.getenv("MESSAGE_DELAY"));
        //System.out.printf("MESSAGE_COUNT: %d \t MESSAGE_DELAY: %d \n", toSendConst, dataInterval);
        workerQueue = new LinkedBlockingQueue<>();
        executorService = new ThreadPoolExecutor(100, 100, 4000, TimeUnit.MILLISECONDS, workerQueue);
        servicesURLs = new URL[serviceURLs.length];
        for(int i = 0; i < serviceURLs.length; ++i) {
            System.out.println(serviceURLs[i]);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            servicesURLs[i] = new URL("http://" + serviceURLs[i]);
        }
    }

    private int randomBetween(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public void start() {
        startTime = System.currentTimeMillis();
        while(toSend > 0) {
            executorService.submit(buildJob(generateData(), servicesURLs[toSend % servicesURLs.length], toSendConst - toSend));
            //System.out.println("ToSend: " + toSend + "\t Queue size: " + workerQueue.size());
            try {
                Thread.sleep(dataInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            --toSend;
        }
    }

    public String generateData() {
        return String.format("{\"id\": \"%d\", \"job\": \"%s\", \"age\": \"%s\", \"salary\": \"%s\"}", idCounting++, job[randomBetween(0, job.length - 1)], randomBetween(18, 67), randomBetween(1500, 6000));
    }

    public Runnable buildJob(String workLoad, URL url, int sendID) {
        long requestTime = System.currentTimeMillis();
        return () -> {
            try {
                HttpURLConnection con = null;

                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = workLoad.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                //System.out.println("done sending");
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    received++;
                    //System.out.printf("Got HTTP-Response! sendID: %d, Response-code: %s, message: %s, from: %s, received: %d", sendID, con.getResponseCode(), response.toString(),url, received);
                    System.out.printf("received: %d queue-size: %d time elapsed: %.2fs \r", received, workerQueue.size(), (System.currentTimeMillis() - requestTime) / 1000f);
                    if(received > toSendConst - 10)
                        System.out.println("Time needed: " + ((System.currentTimeMillis() - startTime) / 1000));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

    }
}
