package com.projet1.client.network;

import com.projet1.shared.model.Message;
import com.projet1.shared.model.User;

public class NetworkAdapter {
    private final NetworkClient networkClient;

    public class NotFoundException extends Exception {
        public NotFoundException() {
            super("Cet utilisateur n'existe pas!");
        }
    }

    public class UnauthorizedException extends Exception {
        public UnauthorizedException() {
            super("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    public class AlreadyConnectedException extends Exception {
        public AlreadyConnectedException() {
            super("Cet utilisateur est déjà connecté.");
        }
    }

    public NetworkAdapter(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public boolean attemptRegister(String username, String password) {
        String command = "REGISTER:" + username + ":" + password;
        String response = networkClient.sendAndReceive(command);
        if (response != null && response.startsWith("SUCCESS")) {
            return true;
        } else {
            return false;
        }
    }

    public User attemptLogin(String username, String password) 
    throws NotFoundException, UnauthorizedException, AlreadyConnectedException {
        String command = "LOGIN:" + username + ":" + password;
        String response = networkClient.sendAndReceive(command);

        if (response == null)
            return null;
        if (response.startsWith("SUCCESS")) {
            String rawUser = response.split(":")[1];
            User user = User.fromBinary(rawUser);
            return user;
        } else if (response.startsWith("ERROR:NOT_FOUND")) {
            throw new NotFoundException();
        } else if (response.startsWith("ERROR:UNAUTHORIZED")) {
            throw new UnauthorizedException();
        } else if (response.startsWith("ERROR:ALREADY_CONNECTED")) {
            throw new AlreadyConnectedException();
        } else {
            return null;
        }
    };

    public User[] fetchContacts() {
        String command = "CONTACT";
        String response = networkClient.sendAndReceive(command);
        if (response != null && response.startsWith("SUCCESS")) {
            String[] parts = response.split(":");
            if (parts.length == 1) {
                return new User[0];
            }
            parts = parts[1].split(";");
            User[] users = new User[parts.length];
            for (int i = 0; i < parts.length; i++) {
                users[i] = User.fromBinary(parts[i]);
            }
            return users;
        } else {
            return new User[0];
        }
    }

    public boolean setMessageRead(Message message) {
        String command = "MSG_READ:" + message.getId();
        String response = networkClient.sendAndReceive(command);
        if (response != null && response.startsWith("SUCCESS")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean sendMessage(Message message) {
        String command = "MSG:" + message.toBinary();
        String response = networkClient.sendAndReceive(command);
        if (response != null && response.startsWith("SUCCESS")) {
            return true;
        } else {
            return false;
        }
    }

    public Message[] fetchMessages(Long userId1, Long userId2) {
        String command = "GET_MESSAGES:" + userId1 + ":" + userId2;
        String response = networkClient.sendAndReceive(command);
        if (response != null && response.startsWith("SUCCESS")) {
            String[] parts = response.split(":");

            if (parts.length == 1) {
                return new Message[0];
            }

            String[] rawMessages = parts[1].split(";");
            Message[] messages = new Message[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = Message.fromBinary(rawMessages[i]);
            }
            return messages;
        } else {
            return null;
        }
    }
}
