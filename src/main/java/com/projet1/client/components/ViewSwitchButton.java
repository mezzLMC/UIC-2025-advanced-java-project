package com.projet1.client.components;

import javax.swing.JButton;

import com.projet1.client.ViewMode;
import com.projet1.client.listeners.ViewSwitchButtonListener;

public class ViewSwitchButton extends JButton {
    public ViewSwitchButton(String text, ViewMode targetMode) {
        super(text);
        this.addActionListener(new ViewSwitchButtonListener(targetMode));
        this.setMaximumSize(new java.awt.Dimension(150, 40));
    }
}
