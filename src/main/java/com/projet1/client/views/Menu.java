package com.projet1.client.views;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.projet1.client.ViewMode;
import com.projet1.client.components.ViewSwitchButton;

public class Menu extends JPanel {

    private final JButton connectButton;
    private final JButton registerButton;

    public Menu() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel headerLabel = new JLabel("Welcome to Projet 1");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectButton = new ViewSwitchButton("Se connecter", ViewMode.LOGIN);
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton = new ViewSwitchButton("S'inscrire", ViewMode.REGISTER);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(headerLabel);
        add(Box.createVerticalStrut(30));
        add(connectButton);
        add(Box.createVerticalStrut(10));
        add(registerButton);
        add(Box.createVerticalGlue());
    }
}
