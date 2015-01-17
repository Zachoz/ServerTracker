package com.zachdekoning.servertracker;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;

/*
 * A Java program to monitor multiple servers or services
 * and use Twilio to send you an SMS alert when one of them
 * goes offline and comes back online.
 *
 * @author Zach de Koning (Zachoz)
 */
public class ServerTracker {

    static String toPhoneNumber;
    private static HashMap<String, Server> servers = new HashMap<String, Server>();
    private static HashMap<String, Server> offlineServers = new HashMap<String, Server>();
    static Timer queryCheckTimer = new Timer();
    static int queryInverval = 30;
    public static boolean sendBackOnlineAlert = true;

    static String configFileLocation = "config.xml"; //relative

    public static void main(String args[]) {
        System.out.println("Starting ServerTracker, by Zach de Koning (Zachoz)");
        System.out.println("Loading configuration...");

        if (System.getProperty("conf") != null) { // Custom config location
            configFileLocation = System.getProperty("conf");
        }

        if (!(new File("config.xml").exists())) {
            copyConfig();
            System.out.println("No config file existed, so one was created for you.");
            System.out.println("Please edit the config file according now and restart this program.");
            System.out.println("Ensure the configuration is done properly as I won't valid it for you.");
            System.exit(0);
        }

        try {
            loadConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(queryInverval);

        queryCheckTimer.scheduleAtFixedRate(new ServerQueryCheck(), queryInverval * 1000, queryInverval * 1000);

        System.out.println("Successfully initialised!");

        endCommand();
    }

    public static HashMap<String, Server> getServers() {
        return servers;
    }

    public static HashMap<String, Server> getOfflineServers() {
        return offlineServers;
    }

    public static void endCommand() {
        Scanner reader = new Scanner(System.in);

        String command;
        while ((command = reader.nextLine()) != null)
            if (command.equals("end")) {
                System.out.println("Shutting down, hopefully your servers don't too!");
                System.exit(0);
            }
    }

    private static void loadConfiguration() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document;

        ///Users/zach_de_koning/javaprojects/ServerTracker/config.xml

        FileInputStream fileStream = new FileInputStream("config.xml");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream));

        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line);

        bufferedReader.close();

        if (!stringBuilder.toString().equals("")) {
            // Build the document
            document = builder.build(new StringReader(stringBuilder.toString()));

            // Get general settings
            Element generalSettings = (Element) document.getRootElement().getChildren("general").get(0);
            queryInverval = Integer.parseInt(generalSettings.getChildText("queryInterval"));
            sendBackOnlineAlert = Boolean.parseBoolean(generalSettings.getChildText("sendBackOnlineAlert"));

            // Get twilio settings
            Element twilioSettings = (Element) document.getRootElement().getChildren("twilio").get(0);
            TwilioUtils.ACCOUNT_SID = twilioSettings.getChildText("sid");
            TwilioUtils.AUTH_TOKEN = twilioSettings.getChildText("authId");
            TwilioUtils.PHONE_NUMBER_FROM = twilioSettings.getChildText("fromPhoneNumber");
            toPhoneNumber = twilioSettings.getChildText("toPhoneNumber");

            // Get Servers
            Element elementServers = (Element) document.getRootElement().getChildren("servers").get(0);
            for (Object item : elementServers.getChildren("server")) {
                Element element = (Element) item;
                String name = element.getChildText("name");
                String hostname = element.getChildText("hostname");
                int port = Integer.parseInt(element.getChildText("port"));

                servers.put(name, new Server(name, hostname, port));
            }
        }
    }

    public static void copyConfig() {
        InputStream stream = ServerTracker.class.getResourceAsStream("/config.xml");
        OutputStream resStreamOut;
        int readBytes;
        byte[] buffer = new byte[4096];
        try {
            resStreamOut = new FileOutputStream(new File("config.xml"));
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }

            stream.close();
            resStreamOut.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
