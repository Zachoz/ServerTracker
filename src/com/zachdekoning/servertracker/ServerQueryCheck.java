package com.zachdekoning.servertracker;

import java.util.Timer;
import java.util.TimerTask;

public class ServerQueryCheck extends TimerTask {

    public void run() {
        long startTime, finishTime;

        System.out.println("Starting server query...");
        startTime = System.currentTimeMillis();

        // Task timeout - timeout after 2 minutes, incase of retrying texts
        new Timer().schedule(new TimerTask() {
            public void run() {
                if (Thread.currentThread().isAlive()) {
                    Thread.currentThread().interrupt();
                }
            }
        }, 120 * 1000);

        for (Server server : ServerTracker.getServers().values()) {
            // If server is offline
            if (!server.query()) {

                // Query it again, to prevent false positives
                System.out.println("Server " + server.getName() + " failed to respond to query. Retrying.");
                try {
                    Thread.sleep(5000); // Sleep for 5 seconds before querying again
                } catch (InterruptedException ex) {
                    // Yolo
                }

                boolean retry = server.query(); // Retry query

                if (!retry && !ServerTracker.getOfflineServers().containsKey(server.getName())) {
                    ServerTracker.getOfflineServers().put(server.getName(), server);

                    for (String phoneNumber : server.getPhoneNumbers()) {
                        boolean sent = false;
                        while (!sent) { // Retry until the SMS successfully sends
                            sent = TwilioUtils.sendSMS(phoneNumber, server.getName() + " is currently offline!");
                        }
                    }
                }

            } else { // If server is online
                // If server was previously online and is now back online
                if (ServerTracker.getOfflineServers().containsKey(server.getName())) {
                    ServerTracker.getOfflineServers().remove(server.getName());
                    if (ServerTracker.sendBackOnlineAlert) {
                        for (String phoneNumber : server.getPhoneNumbers()) {
                            boolean sent = false;
                            while (!sent) { // Retry until the SMS successfully sends
                                sent = TwilioUtils.sendSMS(phoneNumber, server.getName() + " is now back online!");
                            }
                        }
                    }
                }
            }
        }

        finishTime = System.currentTimeMillis();
        System.out.println("Server query complete! Took " + ((finishTime - startTime) / 1000.0) + " seconds");

    }

}
