package com.projet1.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.projet1.client.network.NetworkAdapter;
import com.projet1.shared.model.Message;
import com.projet1.shared.model.User;

public class ChatDataStore {

    private static ChatDataStore instance;
    private NetworkAdapter networkAdapter;
    private User currentUser;
    public Long selectedContactId = null;
    public Map<Long, User> contacts = new HashMap<>();
    public Map<Long, java.util.List<Message>> messages = new HashMap<>();
    private ChatRefreshListener chatRefreshListener = null;
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    public interface ChatRefreshListener {
        void refreshLeftPanel();
        void refreshRightPanel();
    }

    public void setChatRefreshListener(ChatRefreshListener listener) {
        this.chatRefreshListener = listener;
    }

    private ChatDataStore() {}

    public static ChatDataStore getInstance() {
        if (instance == null) {
            instance = new ChatDataStore();
        }
        return instance;
    }

    public ChatDataStore setNetworkAdapter(NetworkAdapter adapter) {
        this.networkAdapter = adapter;
        return this;
    }

    public Long getUserId() {
        return currentUser.getId();
    }
    
    public void appendIncomingMessage(Message message) {
        Long contactId = message.getSenderId();
        if (message.getSenderId() == currentUser.getId()) return;
        messages.get(contactId).add(message);
        if (selectedContactId != null 
        && selectedContactId.equals(contactId)
        && chatRefreshListener != null) {
            chatRefreshListener.refreshRightPanel();
            message.setReceiverRead(true);
            networkExecutor.submit(() -> networkAdapter.setMessageRead(message));
            chatRefreshListener.refreshLeftPanel();
        } else if (chatRefreshListener != null) {
            chatRefreshListener.refreshLeftPanel();
        }
    }

    public void appendOutgoingMessage(String content) {
        Long contactId = selectedContactId;
        Message message = Message.builder()
                .senderId(currentUser.getId())
                .receiverId(contactId)
                .content(content)
                .timestamp(String.valueOf(System.currentTimeMillis() / 1000))
                .receiverRead(false)
                .build();
        messages.get(selectedContactId).add(message);
        networkAdapter.sendMessage(message);
        if (chatRefreshListener != null) {
            chatRefreshListener.refreshRightPanel();
            chatRefreshListener.refreshLeftPanel();
        }
    }

    public String getUsername() {
        return currentUser.getUsername();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        User[] fetchedContacts = networkAdapter.fetchContacts();
        for (User contact : fetchedContacts) {
            contacts.put(contact.getId(), contact);
            Message[] messages = networkAdapter.fetchMessages(currentUser.getId(), contact.getId());
            this.messages.put(contact.getId(), new java.util.ArrayList<>());
            for (Message msg : messages) {
                if (msg != null) {
                    this.messages.get(contact.getId()).add(msg);
                }
            }
        }
    }

    public void setSelectedContactId(Long contactId) {
        selectedContactId = contactId;
        Boolean wasUnread = false;
        java.util.List<Message> contactMessages = messages.get(contactId);
        for (Message msg : contactMessages) {
            if (msg != null) {
                if (msg.getSenderId() != currentUser.getId() && msg.getReceiverRead() == false) {
                    networkAdapter.setMessageRead(msg);
                    msg.setReceiverRead(true);
                    wasUnread = true;
                }
            }
        }
        if (chatRefreshListener != null) {
            if (wasUnread) {
                chatRefreshListener.refreshLeftPanel();
            }
            chatRefreshListener.refreshRightPanel();
        }
    };

    public Message[] getMessagesByContactId(Long contactId) {
        java.util.List<Message> contactMessages = messages.get(contactId);
        if (contactMessages == null) {
            return new Message[0];
        }
        return contactMessages.toArray(new Message[0]);
    }

    public Message[] getMessagesForSelectedContact() {
        java.util.List<Message> contactMessages = messages.get(selectedContactId);
        if (contactMessages == null) {
            return new Message[0];
        }
        return contactMessages.toArray(new Message[0]);
    }

    public void addContact(String userId, String username) {
        if (userId == null || username == null) return;
        Long id = Long.parseLong(userId);
        if (!contacts.containsKey(id)) {
            User newUser = new User();
            newUser.setId(id);
            newUser.setUsername(username);
            contacts.put(id, newUser);
        } else {
            User existingUser = contacts.get(id);
            existingUser.setConnected(true);
        }
        if (chatRefreshListener != null) {
            chatRefreshListener.refreshLeftPanel();
        }
        System.out.println("[CHAT DATA STORE] Added contact: " + username + " (ID: " + userId + ")");
    };

    public void removeContact(String userId) {
        Long id = Long.parseLong(userId);
        User userToRemove = contacts.get(id);
        if (userToRemove != null) {
            contacts.get(id).setConnected(false);
            System.out.println("[CHAT DATA STORE] Marked contact as disconnected: " + userToRemove.getUsername() + " (ID: " + userId + ")");
        }
        if (chatRefreshListener != null) {
            chatRefreshListener.refreshLeftPanel();
        }
    }

    public boolean hasUnreadMessages(User contact) {
        if (contact == null) return false;
        if (contact.getId().equals(currentUser.getId())) return false;
        java.util.List<Message> contactMessages = messages.get(contact.getId());
        if (contactMessages == null) return false;
        for (Message msg : contactMessages) {
            if (msg.getReceiverId().equals(currentUser.getId()) && !msg.getReceiverRead()) {
                System.out.println("[CHAT DATA STORE] Unread message found for contact: " + contact.getUsername() + ". Message ID: " + msg.getId() + ", Content: " + msg.getContent());
                return true;
            }
        }
        return false;
    }

    public User getContactByUsername(String username) {
        for (User user : contacts.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User getContactById(Long id) {
        return contacts.get(id);
    }
}
