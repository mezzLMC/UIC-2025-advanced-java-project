package com.projet1.server;

public class Main {
    public static void main(String[] args) {
        NetworkServer server = new NetworkServer(8080);
        server.start();
    }
}
