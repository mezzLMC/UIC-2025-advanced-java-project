package com.projet1.server.commands;

import java.sql.Connection;
import java.sql.SQLException;
import com.projet1.server.NetworkServer.ClientHandler;
import com.projet1.server.NetworkServer.CommandHandler;
import com.projet1.server.db.DatabaseConnection;
import com.projet1.shared.model.Message;

public class MessageReadCommand implements CommandHandler {

    @Override
    public String getCommandID() {
        return "MSG_READ";
    }

    /**
     * args: [message_id]
     * @return "SUCCESS" or "ERROR:<message>"
     * @throws SQLException 
     */
    @Override
    public String handleCommand(String[] args, ClientHandler client) throws SQLException {

        Connection dbConnection = DatabaseConnection.getConnection();
        dbConnection.isValid(2);

        Message message = Message.builder()
            .id(Long.parseLong(args[0]))
            .build();

        long userId = client.getUserId();
        
        try {
            int ps = dbConnection.prepareStatement(
                "UPDATE messages SET receiver_read = TRUE WHERE id = " + message.getId() + " AND receiver_id = " + userId + ";"
            ).executeUpdate();
            if (ps != 1) {
                return "ERROR:Failed to store message";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR:Failed to store message";
        }

        return "SUCCESS";
    }    
}
