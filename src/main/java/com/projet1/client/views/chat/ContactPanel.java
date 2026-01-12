package com.projet1.client.views.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.projet1.client.ChatDataStore;
import com.projet1.client.listeners.ContactSelectionListener;
import com.projet1.shared.model.Message;
import com.projet1.shared.model.User;

public class ContactPanel extends JPanel {

    private DefaultListModel<String> contactsModel;
    private final ChatDataStore chatDataStore = ChatDataStore.getInstance();
    private JList<String> contactsList;

    public ContactPanel() {
        super(new BorderLayout());
        this.setPreferredSize(new Dimension(200, 0));
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel contactsHeader = new JLabel("Contacts");
        contactsHeader.setFont(new Font("Arial", Font.BOLD, 16));
        contactsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        contactsModel = new DefaultListModel<>();
        for (User contact : chatDataStore.contacts.values()) {
            contactsModel.addElement(contact.getUsername());
        }

        contactsList = new JList<>(contactsModel);
        contactsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        contactsList.addListSelectionListener(new ContactSelectionListener());
        contactsList.setFont(new Font("Arial", Font.PLAIN, 14));
        contactsList.setCellRenderer(new ContactCellRenderer());
        JScrollPane contactsScroll = new JScrollPane(contactsList);

        this.add(contactsHeader, BorderLayout.NORTH);
        this.add(contactsScroll, BorderLayout.CENTER);
    }

    public void refreshData() {
        String selectedValue = contactsList.getSelectedValue();
        contactsModel.clear();

        chatDataStore.contacts.values().stream()
            .sorted(Comparator.comparingLong((User contact) -> {
                Message[] messages = chatDataStore.getMessagesByContactId(contact.getId());
                if (messages == null || messages.length == 0) {
                    return 0L;
                }
                return java.util.Arrays.stream(messages)
                    .mapToLong(m -> Long.parseLong(m.getTimestamp()))
                    .max()
                    .orElse(0L);
            }).reversed())
            .forEach(contact -> contactsModel.addElement(contact.getUsername()));

        if (selectedValue != null) {
            contactsList.setSelectedValue(selectedValue, true);
        }
    }
}
