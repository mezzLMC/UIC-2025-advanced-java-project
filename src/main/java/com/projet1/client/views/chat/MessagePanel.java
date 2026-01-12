package com.projet1.client.views.chat;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.projet1.client.ChatDataStore;
import com.projet1.shared.model.Message;

public class MessagePanel extends JPanel {

    private final JTextArea messagesArea = new JTextArea();
    private final JScrollPane messagesScroll = new JScrollPane(messagesArea);
    private final ChatDataStore chatDataStore = ChatDataStore.getInstance();
    private final JLabel messagesHeader = new JLabel("Messages");
    private final JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
    private final JTextField messageInput = new JTextField();
    private final JButton sendButton = new JButton("Envoyer");


    public MessagePanel() {
        super(new BorderLayout());

        messagesHeader.setFont(new Font("Arial", Font.BOLD, 16));
        messagesHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        messagesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messagesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messagesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        messageInput.setFont(new Font("Arial", Font.PLAIN, 14));
        messageInput.setEnabled(false);
        messageInput.addActionListener(e -> submitMessage());

        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        sendButton.addActionListener(e -> submitMessage());
        sendButton.setEnabled(false);

        this.add(messagesHeader, BorderLayout.NORTH);
        this.add(messagesScroll, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void submitMessage() {
        String content = messageInput.getText().trim();
        if (!content.isEmpty()) {
            chatDataStore.appendOutgoingMessage(content);
            messageInput.setText("");
        }
        messageInput.requestFocusInWindow();
    }

    public void refreshData() {
        messagesArea.setText("");

        if (chatDataStore.selectedContactId == null) {
            messagesArea.append("Sélectionnez un contact pour commencer à discuter.");
            return;
        }

        messageInput.setEnabled(true);
        sendButton.setEnabled(true);

        for (Message msg : chatDataStore.getMessagesForSelectedContact()) {
            if (msg == null) continue;
            String senderName = (msg.getSenderId() == chatDataStore.getUserId()) ? "Moi" : 
                                chatDataStore.contacts.get(msg.getSenderId()).getUsername();
            String formattedMessage = MessageFormatter.formatMessage(senderName, msg);
            messagesArea.append(formattedMessage);
            messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
        }
    }
}
