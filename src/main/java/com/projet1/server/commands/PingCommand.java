package com.projet1.server.commands;

import com.projet1.server.NetworkServer.ClientHandler;
import com.projet1.server.NetworkServer.CommandHandler;

public class PingCommand implements CommandHandler {
    @Override
    public String getCommandID() {
        return "PING";
    }

    @Override
    public String handleCommand(String[] args, ClientHandler client) {
        return "PONG";
    }    
}
