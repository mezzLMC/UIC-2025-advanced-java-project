package com.projet1.client;

public enum ViewMode {
    MENU("Bienvenue dans l'application", "Menu"),
    LOGIN("Prêt à vous connecter", "Connectez-vous"),
    REGISTER("Prêt à vous inscrire", "Inscrivez-vous"),
    CHAT("Connecté au chat", "Discutez avec vos amis");
    
    private final String statusMessage;
    private final String viewTitle;

    ViewMode(String statusMessage, String viewTitle) {
        this.statusMessage = statusMessage;
        this.viewTitle = viewTitle;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getViewTitle() {
        return viewTitle;
    }
}
