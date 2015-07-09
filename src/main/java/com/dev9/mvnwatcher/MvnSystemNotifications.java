package com.dev9.mvnwatcher;


import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MvnSystemNotifications {

    private boolean gui;

    SystemTray tray;

    private TrayIcon trayIcon;

    PopupMenu popup = new PopupMenu();

    MenuItem messageMenu = new MenuItem("Starting...");

    private Image IMAGE_OK;
    private Image IMAGE_FAIL;
    private Image IMAGE_WORKING;

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

    public void setupImages() {
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

                popup.add(messageMenu);

                trayIcon.setPopupMenu(popup);
            } catch (AWTException e) {
                System.err.println("Unable to start system tray integration.");
                e.printStackTrace();
                gui = false;
            }
        }
    }

    public void update(String message, Status status) {
        if (gui) {
            trayIcon.setImage(statusToImage(status));
            trayIcon.setToolTip(message);
            messageMenu.setLabel(message);

        } else {
            System.out.println(message);
        }
    }


    //Obtain the image URL
    protected Image createImage(String path, String description) {
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
