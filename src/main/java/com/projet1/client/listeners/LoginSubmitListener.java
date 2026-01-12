package com.projet1.client.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.projet1.client.AppFrame;
import com.projet1.client.network.NetworkAdapter;
import com.projet1.shared.model.User;

public class LoginSubmitListener implements ActionListener {

    private final JTextField userNameField;
    private final JPasswordField passwordField;
    private final AppFrame mainFrame;

    public LoginSubmitListener(JTextField userNameField, JPasswordField passwordField, AppFrame mainFrame) {
        this.userNameField = userNameField;
        this.passwordField = passwordField;
        this.mainFrame = mainFrame;
    }


    public void actionPerformed(ActionEvent e) {

        String username = userNameField.getText();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.");
            return;
        }
        try {
            User user = mainFrame.networkAdapter.attemptLogin(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(null, "Erreur réseau");
            } else {
                mainFrame.setLoggedIn(user);
            }
            } catch (NetworkAdapter.NotFoundException | NetworkAdapter.UnauthorizedException | NetworkAdapter.AlreadyConnectedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return;
        }

    }
}
