package com.projet1.server.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.projet1.server.NetworkServer;
import com.projet1.server.NetworkServer.ClientHandler;
import com.projet1.server.NetworkServer.CommandHandler;
import com.projet1.server.db.DatabaseConnection;
import com.projet1.shared.model.User;

public class ContactCommand implements CommandHandler {

    @Override
    public String getCommandID() {
        return "CONTACT";
    }
    
    /**
     * args: [username, password]
     * @return "SUCCESS:<user_id>" or "ERROR:<message>"
     * @throws SQLException
    */
    @Override
    public String handleCommand(String[] args, ClientHandler client) throws SQLException {


        Connection dbConnection = DatabaseConnection.getConnection();
        dbConnection.isValid(2);
        String sql = "SELECT id, username, connected FROM users";
        PreparedStatement stmt = dbConnection.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        StringBuilder contacts = new StringBuilder();
        while (rs.next()) {
            User user = User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString("username"))
            .connected(rs.getBoolean("connected"))
            .build();
            contacts.append(
                user.getId()).append(",").append(user.getUsername()).append(",").append(NetworkServer.isClientConnected(user.getId()) ? "1" : "0").append(";");
        }

        return "SUCCESS:" + contacts.toString();
    }
}
