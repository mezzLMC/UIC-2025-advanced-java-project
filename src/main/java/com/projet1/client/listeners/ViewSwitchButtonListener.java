package com.projet1.client.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import com.projet1.client.AppFrame;
import com.projet1.client.ViewMode;

public class ViewSwitchButtonListener implements ActionListener {

    private ViewMode targetMode;

    public ViewSwitchButtonListener(ViewMode targetMode) {
        this.targetMode = targetMode;
    }

    public void actionPerformed(ActionEvent e) {
        AppFrame mainFrame = (AppFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
        mainFrame.setViewMode(targetMode);
    }
}
