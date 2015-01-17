package com.zachdekoning.servertracker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Server {

    private String name;
    private String hostname;
    private int port;

    public Server(String name, String hostname, int port) {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
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

    public boolean query() {
        boolean online = true;

        try {
            Socket s = new Socket();
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(this.hostname, this.port);
            s.connect(sa, 3000);
            s.close();
        } catch (IOException e) {
            online = false;
        }

        return online;
    }

}
