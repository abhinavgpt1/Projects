package com.example.milkman_legacy;

import com.example.milkman_legacy.login.LoginApplication;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(LoginApplication.class, args);

        // Sample VM Options
        // -ea --module-path "<Path to javafx lib folder>" --add-modules javafx.controls,javafx.fxml,javafx.media --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow --enable-native-access=javafx.graphics --enable-native-access=javafx.media
    }
}
