package com.projet1.server;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.projet1.server.commands.ContactCommand;
import com.projet1.server.commands.GetMessages;
import com.projet1.server.commands.LoginCommand;
import com.projet1.server.commands.MessageReadCommand;
import com.projet1.server.commands.PingCommand;
import com.projet1.server.commands.RegisterCommand;
import com.projet1.server.commands.SendCommand;

@Getter
@Setter
public class NetworkServer {

    public interface CommandHandler {
        String getCommandID();
        String handleCommand(String[] args, ClientHandler clientHandler) throws SQLException; 
    }
    
    private int port;
    private ServerSocket serverSocket;
    private boolean running;
    private ExecutorService threadPool;
    private static final Map<Long, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    
    private static List<CommandHandler> commandHandlers = List.of(
        new LoginCommand(),
        new PingCommand(),
        new RegisterCommand(),
        new ContactCommand(),
        new SendCommand(),
        new GetMessages(),
        new MessageReadCommand()
    );
    
    public NetworkServer(int port) {
        this.port = port;
        this.running = false;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public static Boolean isClientConnected(Long userId) {
        return connectedClients.containsKey(userId);
    }
    
    /**
     * Start the server and listen for connections.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("[NETWORK SERVER] Server started on port " + port);
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[NETWORK SERVER] New client connected: " + clientSocket.getInetAddress());
                    threadPool.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("[NETWORK SERVER] Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[NETWORK SERVER] Could not start server: " + e.getMessage());
        }
    }

    public static boolean sendToClient(Long userId, String message) {
        ClientHandler client = connectedClients.get(userId);

        System.out.println("[NETWORK SERVER] Sending to client " + userId + ": " + message);

        if (client != null) {
            try {
                PrintWriter out = new PrintWriter(client.clientSocket.getOutputStream(), true);
                out.println(message);
                return true;
            } catch (IOException e) {
                System.err.println("[NETWORK SERVER] Error sending message to client " + userId + ": " + e.getMessage());
            }
        }
        return false;
    }

        /**
     * Broadcast a message to all connected clients.
     */
    public static void broadcast(String message) {
        for (ClientHandler client : connectedClients.values()) {
            client.sendMessage(message);
        }
    }

    /**
     * Register a client after successful login.
     */
    public static void registerClient(Long userId, ClientHandler client) {
        connectedClients.put(userId, client);
    }
    
    /**
     * Unregister a client on disconnect.
     */
    public static void unregisterClient(Long userId) {
        connectedClients.remove(userId);

        NetworkServer.broadcast("DISCONNECTED:" + userId);        
    }
    
    /**
     * Stop the server.
     */
    public void stop() {
        running = false;
        threadPool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("[NETWORK SERVER] Server stopped.");
        } catch (IOException e) {
            System.err.println("[NETWORK SERVER] Error stopping server: " + e.getMessage());
        }
    }
    
    /**
     * Handler for individual client connections.
     */
    public static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private Long userId = null;
        private PrintWriter out;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            System.out.println("[NETWORK SERVER] New client handler created for " + socket.getInetAddress());
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
            NetworkServer.registerClient(userId, this);
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
        
        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("[NETWORK SERVER] Received message: " + message);
                    String[] parts = message.split(":");
                    String commandID = parts[0];
                    String[] args = java.util.Arrays.copyOfRange(parts, 1, parts.length);
                    boolean commandFound = false;
                    for (CommandHandler handler : commandHandlers) {
                        if (handler.getCommandID().equalsIgnoreCase(commandID)) {
                            try {
                                String response = handler.handleCommand(args, this);
                                System.out.println("[NETWORK SERVER] Command " + commandID + " executed with response: " + response.subSequence(0, Math.min(response.length(), 50)) + "...");
                                out.println(response);
                            } catch (SQLException e) {
                                out.println("ERROR: Database error - " + e.getMessage());
                            }
                            commandFound = true;
                            break;
                        }
                    }
                    if (!commandFound) {
                        out.println("Unknown command: " + commandID);
                    }
                }
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                try {
                    if (userId != null) {
                        NetworkServer.unregisterClient(userId);
                    }
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}
