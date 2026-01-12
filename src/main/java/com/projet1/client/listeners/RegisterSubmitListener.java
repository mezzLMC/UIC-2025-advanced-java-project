package com.projet1.client.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.projet1.client.AppFrame;
import com.projet1.client.ViewMode;

public class RegisterSubmitListener implements ActionListener {

    private final JTextField userNameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final AppFrame appFrame;

    public RegisterSubmitListener(JTextField userNameField, JPasswordField passwordField, JPasswordField confirmPasswordField, AppFrame appFrame) {
        this.userNameField = userNameField;
        this.passwordField = passwordField;
        this.confirmPasswordField = confirmPasswordField;
        this.appFrame = appFrame;
    }

    public void actionPerformed(ActionEvent e) {
        String username = userNameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Les mots de passe ne correspondent pas.");
            return;
        }
        boolean success = appFrame.networkAdapter.attemptRegister(username, password);
        if (success) {
            JOptionPane.showMessageDialog(null, "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            appFrame.setViewMode(ViewMode.LOGIN);
        } else {
            JOptionPane.showMessageDialog(null, "Échec de l'inscription. Veuillez réessayer.");
        }
    }
}
