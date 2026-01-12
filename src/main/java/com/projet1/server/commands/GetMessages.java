package com.projet1.server.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.projet1.server.NetworkServer.ClientHandler;
import com.projet1.server.NetworkServer.CommandHandler;
import com.projet1.server.db.DatabaseConnection;
import com.projet1.shared.model.Message;

public class GetMessages implements CommandHandler {

    @Override
    public String getCommandID() {
        return "GET_MESSAGES";
    }
    
    /**
     * args: [user1_id, user2_id]
     * @return "SUCCESS:<messages>" or "ERROR:<message>"
     * format of <messages>: sender_id,receiver_id,timestamp,content;...
     * @throws SQLException
    */
    @Override
    public String handleCommand(String[] args, ClientHandler client) throws SQLException {
        
        Connection dbConnection = DatabaseConnection.getConnection();
        dbConnection.isValid(2);
        String sql = "SELECT id, sender_id, receiver_id, receiver_read, created_at, content FROM messages WHERE " +
                     "(sender_id = ? AND receiver_id = ?) OR " +
                     "(sender_id = ? AND receiver_id = ?)";
        PreparedStatement stmt = dbConnection.prepareStatement(sql);
        stmt.setLong(1, Long.parseLong(args[0]));
        stmt.setLong(2, Long.parseLong(args[1]));
        stmt.setLong(3, Long.parseLong(args[1]));
        stmt.setLong(4, Long.parseLong(args[0]));

        ResultSet rs = stmt.executeQuery();

        StringBuilder messages = new StringBuilder();
        while (rs.next()) {
            long timestampSeconds = rs.getTimestamp("created_at").getTime() / 1000;
            Message message = Message.builder()
                .id(rs.getLong("id"))
                .senderId(rs.getLong("sender_id"))
                .receiverId(rs.getLong("receiver_id"))
                .content(rs.getString("content"))
                .receiverRead(rs.getBoolean("receiver_read"))
                .timestamp(String.valueOf(timestampSeconds))
                .build();
            messages.append(message.toBinary()).append(";");
        }

        return "SUCCESS:" + messages.toString();   
    }
}
