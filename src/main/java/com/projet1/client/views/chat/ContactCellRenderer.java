package com.projet1.client.views.chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.projet1.client.ChatDataStore;
import com.projet1.shared.model.User;

public class ContactCellRenderer extends JPanel implements ListCellRenderer<String> {

    private final ChatDataStore chatDataStore = ChatDataStore.getInstance();
    private final JLabel statusLabel;
    private final JLabel usernameLabel;

    public ContactCellRenderer() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setOpaque(true);
        
        statusLabel = new JLabel("●");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        add(statusLabel);
        add(usernameLabel);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
            boolean isSelected, boolean cellHasFocus) {
        
        User contact = chatDataStore.getContactByUsername(value);

        usernameLabel.setText(chatDataStore.getUsername().equals(value) ? value + " (Vous)" : value);
        
        if (contact.isConnected()) {
            statusLabel.setForeground(new Color(0, 200, 0)); // Green
        } else {
            statusLabel.setForeground(Color.GRAY);
        }
        
        if (chatDataStore.hasUnreadMessages(contact)) {
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        } else {
            usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        }
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            usernameLabel.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            usernameLabel.setForeground(list.getForeground());
        }
        
        return this;
    }
}
