package com.projet1.client.views;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.projet1.client.AppFrame;
import com.projet1.client.ViewMode;
import com.projet1.client.components.ViewSwitchButton;
import com.projet1.client.listeners.LoginSubmitListener;

public class Login extends JPanel {

    private final JLabel headerLabel;
    private final JLabel userNameLabel;
    private final JTextField userNameField;

    private final JLabel passwordLabel;
    private final JPasswordField passwordField;

    private final JButton loginButton;
    private final JButton registerButton;
    private final LoginSubmitListener loginListener;

    public Login(AppFrame appFrame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        headerLabel = new JLabel("Connectez vous à votre compte");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userNameLabel = new JLabel("Nom d'utilisateur:");
        userNameField = new JTextField();
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userNameField.setMaximumSize(new java.awt.Dimension(300, 30));
        userNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordLabel = new JLabel("Mot de passe:");
        passwordField = new JPasswordField();
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new java.awt.Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginListener = new LoginSubmitListener(userNameField, passwordField, appFrame);
        passwordField.addActionListener(loginListener);

        loginButton = new JButton("Se connecter");
        loginButton.setMaximumSize(new java.awt.Dimension(150, 30));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(loginListener);

        registerButton = new ViewSwitchButton("S'inscrire", ViewMode.REGISTER);
        registerButton.setMaximumSize(new java.awt.Dimension(150, 30));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(headerLabel);
        add(Box.createVerticalStrut(30));
        add(userNameLabel);
        add(userNameField);
        add(Box.createVerticalStrut(20));
        add(passwordLabel);
        add(passwordField);
        add(Box.createVerticalStrut(30));
        add(loginButton);
        add(Box.createVerticalStrut(10));
        add(registerButton);
        add(Box.createVerticalGlue());
    }
}
