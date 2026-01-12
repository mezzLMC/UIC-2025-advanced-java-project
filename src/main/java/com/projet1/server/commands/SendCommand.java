package com.projet1.server.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.projet1.server.NetworkServer;
import com.projet1.server.NetworkServer.ClientHandler;
import com.projet1.server.NetworkServer.CommandHandler;
import com.projet1.server.db.DatabaseConnection;
import com.projet1.shared.model.Message;

public class SendCommand implements CommandHandler {

    @Override
    public String getCommandID() {
        return "MSG";
    }

    /**
     * args: [sender_id, recipient_id, message, timestamp]
     * @return "SUCCESS" or "ERROR:<message>"
     * @throws SQLException 
     */
    @Override
    public String handleCommand(String[] args, ClientHandler client) throws SQLException {

        Connection dbConnection = DatabaseConnection.getConnection();
        dbConnection.isValid(2);

        Message message = Message.fromBinary(args[0]);

        Timestamp sqlTimestamp = new Timestamp(Long.parseLong(message.getTimestamp()) * 1000L);

        try {
            PreparedStatement ps = dbConnection.prepareStatement(
                "INSERT INTO messages (sender_id, receiver_id, content, created_at) VALUES (?, ?, ?, ?);",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, message.getSenderId());
            ps.setLong(2, message.getReceiverId());
            ps.setString(3, message.getContent());
            ps.setTimestamp(4, sqlTimestamp);

            int psResult = ps.executeUpdate();
            if (psResult != 1) {
                return "ERROR:Failed to store message";
            }
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                message.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR:Failed to store message";
        }


        NetworkServer.sendToClient(message.getReceiverId(), 
            "MSG_FROM:" + message.toBinary()
        );

        return "SUCCESS";
    }    
}
