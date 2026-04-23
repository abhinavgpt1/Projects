package com.example.milkman_legacy.util;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class UIHelper {
    public static void showAlert(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    public static String getDesktopPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String desktopPath;
        if (osName.contains("win")) {
            desktopPath = System.getProperty("user.home") + "\\Desktop";
        } else {
            desktopPath = System.getProperty("user.home");
        }
        return desktopPath;
    }
}
