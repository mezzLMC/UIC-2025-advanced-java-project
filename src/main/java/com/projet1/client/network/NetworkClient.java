package com.projet1.client.network;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * TCP Network client for connecting to a server.
 */
@Getter
@Setter
public class NetworkClient {
    
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;
    private Thread listenerThread;
    private Consumer<String> messageHandler;
    public final NetworkAdapter networkAdapter = new NetworkAdapter(this);

    private final BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
    private static final String[] ASYNC_PREFIXES = {"MSG_FROM:", "CONNECTED:", "DISCONNECTED:"};
    
    public NetworkClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
        this.connected = false;
    }

    public void setMessageHandler(Consumer<String> handler) {
        System.out.println("[NETWORK CLIENT] Setting message handler.");
        this.messageHandler = handler;
    }

    public boolean connect() {
        System.out.println("[NETWORK CLIENT] Connecting to server " + serverHost + ":" + serverPort);
        try {
            socket = new Socket(serverHost, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
                System.out.println("[NETWORK CLIENT] Connected to server: " + serverHost + ":" + serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    private void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        }
    }

    private boolean isAsyncMessage(String message) {
        for (String prefix : ASYNC_PREFIXES) {
            if (message.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public void startListening() {
        listenerThread = new Thread(() -> {
            try {
                while (connected && in != null) {
                    String message = in.readLine();
                    if (message != null) {
                        if (isAsyncMessage(message)) {
                            System.out.println("[NETWORK CLIENT] Received async message: \"" + message + "\"");
                            if (messageHandler != null) {
                                messageHandler.accept(message);
                            }
                        } else {
                            responseQueue.offer(message);
                        }
                    }
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("Error in listener thread: " + e.getMessage());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Send a command and wait for response (synchronous).
     * 
     * @param command the command to send
     * @param timeoutSeconds max wait time
     * @return the response, or null if timeout
     */
    public String sendAndReceive(String command, int timeoutSeconds) {
        // Clear any stale responses
        responseQueue.clear();
        
        System.out.println("[NETWORK CLIENT] Sending command: \"" + command + "\"");
        sendMessage(command);
        
        try {
            String response = responseQueue.poll(timeoutSeconds, TimeUnit.SECONDS);
            System.out.println("[NETWORK CLIENT] Received response: \"" + response.substring(0, Math.min(response.length(), 50)) + "...\"");
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public String sendAndReceive(String command) {
        return sendAndReceive(command, 10);
    }
    
    public void disconnect() {
        connected = false;
        try {
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("[NETWORK CLIENT] Disconnected from server.");
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
