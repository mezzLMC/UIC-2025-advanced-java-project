package com.projet1.client;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        final String host;
        final int port;

        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            host = "localhost";
            port = 8080;
        }
    
        SwingUtilities.invokeLater(() -> {
            AppFrame frame = new AppFrame(host, port);
            frame.setVisible(true);
        });
    }
}
