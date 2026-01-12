package com.projet1.server.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.projet1.server.NetworkServer.ClientHandler;
import com.projet1.server.NetworkServer.CommandHandler;
import com.projet1.server.db.DatabaseConnection;

public class RegisterCommand implements CommandHandler {

    @Override
    public String getCommandID() {
        return "REGISTER";
    }

    /**
     * args: [username, password]
     * @return "SUCCESS" or "ERROR:<message>"
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
        String sql = "SELECT id, username, password, connected FROM users WHERE username = ?";
        PreparedStatement stmt = dbConnection.prepareStatement(sql);
        stmt.setString(1, username);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return "ERROR: Username already exists";
        }   

        sql = "INSERT INTO users (username, password, connected) VALUES (?, ?, FALSE)";
        stmt = dbConnection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.executeUpdate();

        return "SUCCESS";
    }
}
