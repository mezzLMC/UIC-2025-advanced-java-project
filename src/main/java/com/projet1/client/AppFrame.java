package com.projet1.client;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.projet1.client.network.NetworkAdapter;
import com.projet1.client.network.NetworkClient;
import com.projet1.client.views.*;
import com.projet1.client.views.Menu;
import com.projet1.client.views.chat.*;
import com.projet1.shared.model.Message;
/**
 * Main application window using Swing.
 */
public class AppFrame extends JFrame {

    private ViewMode currentViewMode = ViewMode.MENU;
    private JPanel currentPanel;
    private final NetworkClient networkClient;
    private final Map<ViewMode, JPanel> viewPanels = new HashMap<>();
    public final NetworkAdapter networkAdapter;
    private final ChatDataStore chatDataStore = ChatDataStore.getInstance();
    JLabel statusBar = new JLabel(" Status: " + currentViewMode.getStatusMessage());

    public void setViewMode(ViewMode mode) {
        this.currentViewMode = mode;
        setTitle("Projet 1 - " + mode.getViewTitle());
        switchPanel(viewPanels.get(mode));
        statusBar.setText(" Status: " + mode.getStatusMessage());
    }

    public AppFrame(String host, int port) {
        this.networkClient = new NetworkClient(host, port);
        networkClient.setMessageHandler(this::handleIncomingMessage);
        networkClient.connect();
        networkClient.startListening();
        this.networkAdapter = new NetworkAdapter(networkClient);

        viewPanels.put(ViewMode.MENU, new Menu());
        viewPanels.put(ViewMode.LOGIN, new Login(this));
        viewPanels.put(ViewMode.REGISTER, new Register(this));
        viewPanels.put(ViewMode.CHAT, new Chat(this));
        initComponents();
        setViewMode(ViewMode.MENU);
    }

    public void setLoggedIn(com.projet1.shared.model.User user) {
        chatDataStore.setCurrentUser(user);
        setViewMode(ViewMode.CHAT);
    }

    private void handleIncomingMessage(String message) {
        if (message.startsWith("MSG_FROM:")) {
            String rawMessage = message.substring(9);
            Message msg = Message.fromBinary(rawMessage);
            chatDataStore.appendIncomingMessage(msg);
        };

        if (message.startsWith("CONNECTED:")) {
            String[] parts = message.substring(10).split(",", 2);
            if (parts.length >= 2) {
                String userId = parts[0];
                String username = parts[1];

                chatDataStore.addContact(userId, username);
            }
        }

        if (message.startsWith("DISCONNECTED:")) {
            String userId = message.substring(13);
            chatDataStore.removeContact(userId);
        }
    }

    private void switchPanel(JPanel newPanel) {
        if (currentPanel != null) {
            getContentPane().remove(currentPanel);
        }
        currentPanel = newPanel;
        getContentPane().add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        chatDataStore.setNetworkAdapter(networkAdapter);
    }
}
