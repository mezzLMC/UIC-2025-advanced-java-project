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

public class LoginCommand implements CommandHandler {

    @Override
    public String getCommandID() {
        return "LOGIN";
    }

    
    /**
     * args: [username, password]
     * @return "SUCCESS:<user_id>" or "ERROR:<message>"
     * @throws SQLException
    */
    @Override
    public String handleCommand(String[] args, ClientHandler client) throws SQLException {

        if (args.length < 2) {
            return "ERROR: Missing arguments";
        }

        String username = args[0];
        String password = args[1];
        Connection dbConnection = DatabaseConnection.getConnection();
        dbConnection.isValid(2);

        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        PreparedStatement stmt = dbConnection.prepareStatement(sql);
        stmt.setString(1, username);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return "ERROR:NOT_FOUND";
        }

        User user = User.builder()
        .id(rs.getLong("id"))
        .username(rs.getString("username"))
        .password(rs.getString("password"))
        .build();

        if (!user.getPassword().equals(password)) {
            return "ERROR:INVALID_CREDENTIALS";
        }

        if (NetworkServer.isClientConnected(user.getId())) {
            return "ERROR:ALREADY_CONNECTED";
        }        
        
        client.setUserId(user.getId());
        NetworkServer.broadcast("CONNECTED:" + user.getId() + "," + user.getUsername());

        return "SUCCESS:" + user.getId() + "," + user.getUsername() + "," + "1";
    }
}
