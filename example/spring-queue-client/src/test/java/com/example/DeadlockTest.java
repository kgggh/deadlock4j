package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class DeadlockTest {

    private static final String REQ_URL = "http://localhost:8181/deadlock-test/db";
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(DeadlockTest::sendRequest);
        }

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Total errors (500 errors): " + counter.get());
    }

    private static void sendRequest() {
        try {
            URL url = new URL(REQ_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == 500) {
                System.out.println("Server Error 500 Detected!!!");
                counter.incrementAndGet();
            }

        } catch (Exception e) {
            counter.incrementAndGet();
        }
    }
}
