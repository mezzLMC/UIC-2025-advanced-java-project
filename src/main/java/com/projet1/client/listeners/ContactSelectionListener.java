package com.projet1.client.listeners;

import com.projet1.client.ChatDataStore;
import com.projet1.shared.model.User;

public class ContactSelectionListener implements javax.swing.event.ListSelectionListener {

    private final ChatDataStore chatDataStore = ChatDataStore.getInstance();

    @Override
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        javax.swing.JList<?> list = (javax.swing.JList<?>) e.getSource();
        String selectedUsername = (String) list.getSelectedValue();
        if (selectedUsername != null) {
            User selectedContact = chatDataStore.getContactByUsername(selectedUsername);
            if (selectedContact != null) {
                chatDataStore.setSelectedContactId(selectedContact.getId());
            }
        }
    }
}
