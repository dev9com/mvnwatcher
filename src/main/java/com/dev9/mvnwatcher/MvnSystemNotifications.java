package com.dev9.mvnwatcher;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MvnSystemNotifications {

    private boolean gui;

    MvnMonitor monitor;

    SystemTray tray;
    private TrayIcon trayIcon;

    PopupMenu popup = new PopupMenu();
    MenuItem messageMenu = new MenuItem("Starting...");
    MenuItem exceptionMenu = new MenuItem("Last Exception");

    private Image IMAGE_OK;
    private Image IMAGE_FAIL;
    private Image IMAGE_WORKING;

    private String lastUpdate = "-";

    public String status() {
        return lastUpdate;
    }

    public MvnSystemNotifications(MvnMonitor monitor) {
        this.monitor = monitor;
    }

    public enum Status {
        OK, FAIL, WORKING
    }

    private Image statusToImage(Status status) {
        switch (status) {
            case OK:
                return IMAGE_OK;
            case FAIL:
                return IMAGE_FAIL;
            case WORKING:
                return IMAGE_WORKING;
        }
        return IMAGE_FAIL;
    }

    private void setupImages() {
        IMAGE_OK = createImage("/images/OK.png", "Running Normally");
        IMAGE_FAIL = createImage("/images/FAIL.png", "Build Failing");
        IMAGE_WORKING = createImage("/images/WORKING.png", "Building...");
    }

    public void init(boolean useGui) {
        gui = java.awt.SystemTray.isSupported();

        if (useGui == false && gui == true)
            gui = false;

        if (gui == true) {

            tray = SystemTray.getSystemTray();

            setupImages();

            trayIcon = new TrayIcon(IMAGE_WORKING);

            try {
                tray.add(trayIcon);

                messageMenu.setEnabled(false);

                popup.add(messageMenu);

                MenuItem quitMenu = new MenuItem();
                quitMenu.setLabel("Quit");
                quitMenu.addActionListener(e -> monitor.shutdown = true);

                popup.add(quitMenu);

                trayIcon.setPopupMenu(popup);
            } catch (AWTException e) {
                System.err.println("Unable to start system tray integration.");
                e.printStackTrace();
                gui = false;
            }
        }
    }

    public void update(String message, Status status) {
        update(message, status, null);
    }

    public void update(String message, Status status, Exception e) {

        if (message == null)
            return;
        if (status == null)
            return;

        String newMessage;

        if (e == null)
            newMessage = message + ":" + status.toString();
        else
            newMessage = message + ":" + status.toString() + ":" + e.getMessage();

        if (lastUpdate.compareTo(newMessage) == 0) {
            return;
        }

        lastUpdate = newMessage;

        if (gui) {
            trayIcon.setImage(statusToImage(status));
            trayIcon.setToolTip(message);
            messageMenu.setLabel(message);

            if (e == null) {
                if (exceptionMenu.getParent() != null)
                    popup.remove(exceptionMenu);
            } else {
                if (exceptionMenu.getParent() == null)
                    popup.add(exceptionMenu);
                exceptionMenu.setLabel(e.getLocalizedMessage());
            }
        }

        System.out.println(message);
        if (e != null)
            e.printStackTrace();
    }


    //Obtain the image URL
    private Image createImage(String path, String description) {
        URL imageURL = MvnSystemNotifications.class.getResource(path);

        if (imageURL == null) {
            gui = false;
            System.err.println("Can't boot system tray, resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

}
