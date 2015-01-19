package com.zachdekoning.servertracker;

import com.zachdekoning.servertracker.mcping.mcping.MinecraftPing;
import com.zachdekoning.servertracker.mcping.mcping.MinecraftPingOptions;
import com.zachdekoning.servertracker.mcping.mcping.MinecraftPingReply;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;

public class Server {

    private String name;
    private String hostname;
    private int port;
    private ArrayList<String> phoneNumbers = new ArrayList<String>();
    private ServiceType serviceType = ServiceType.TCP;
    private ArrayList<String> unexpectedContent = new ArrayList<String>();

    public Server(String name, String hostname, int port, ServiceType serviceType) {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.serviceType = serviceType;
    }

    public String getName() {
        return this.name;
    }

    public String getHost() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public ArrayList<String> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public ArrayList<String> getUnexpectedContent() {
        return this.unexpectedContent;
    }

    public boolean query() {
        if (getServiceType() == ServiceType.MINECRAFT) return minecraftPing();
        if (getServiceType() == ServiceType.HTTP) return httpQuery();
        return tcpPing();
    }

    private boolean tcpPing() {
        boolean online = false;

        try {
            Socket s = new Socket();
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(this.hostname, this.port);
            s.connect(sa, ServerTracker.queryTimeOut);
            if (s.isConnected()) {
                s.close();
                online = true;
            }
        } catch (IOException e) {
            online = false;
        }

        return online;
    }

    private boolean minecraftPing() {
        boolean online = true;

        try {
            MinecraftPingOptions options = new MinecraftPingOptions();
            options.setHostname(this.hostname);
            options.setPort(this.port);
            MinecraftPingReply ping = new MinecraftPing().getPing(options);
        } catch (Exception ex) {
            online = false;
        }

        return online;
    }

    private boolean httpQuery() {
        try {
            URL url = new URL(this.getHost());
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);

            if (this.unexpectedContent.contains("triggerAlertForBlankPage") && body.equals(""))
                return false;

            for (String unexpectedOutput : this.unexpectedContent) {
                if (body.contains(unexpectedOutput)) {
                    return false;
                }
            }

        } catch (IOException ex) {
            return false;
        }

        return true;
    }

}
