package com.projet1.client.views.chat;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.projet1.client.AppFrame;
import com.projet1.client.ChatDataStore;
import com.projet1.client.ChatDataStore.ChatRefreshListener;

public class Chat extends JPanel implements ChatRefreshListener {

    @SuppressWarnings("unused")
    private final AppFrame appFrame;
    private final ChatDataStore chatDataStore = ChatDataStore.getInstance();
    private final ContactPanel contactsPanel = new ContactPanel();
    private final MessagePanel messagesPanel = new MessagePanel();

    public void refreshLeftPanel() {
        System.out.println("Refreshing left panel");
        SwingUtilities.invokeLater(() -> {
            contactsPanel.refreshData();
        });
    }

    public void refreshRightPanel() {
        System.out.println("Refreshing right panel");
        SwingUtilities.invokeLater(() -> {
            messagesPanel.refreshData();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        chatDataStore.setChatRefreshListener(this);
        this.refreshLeftPanel();
        this.refreshRightPanel();
    }

    public Chat(AppFrame appFrame) {
        this.appFrame = appFrame;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(contactsPanel, BorderLayout.WEST);
        this.add(messagesPanel, BorderLayout.CENTER);
    }
}
