package com.example.milkman_legacy;

public class Constants {
    // Basic paths
    public static final String ROOT_PATH = "/com/example/milkman_legacy";
    public static final String ASSETS = ROOT_PATH + "/assets";
    public static final String ASSETS_AUDIOCLIPS = ASSETS + "/audioclips";
    public static final String LOGIN = ROOT_PATH + "/login";
    public static final String DASHBOARD = ROOT_PATH + "/dashboard";

    // Derived paths - fxml
    public static final String LOGIN_VIEW = LOGIN + "/LoginView.fxml";
    public static final String DASHBOARD_VIEW = DASHBOARD + "/DashboardView.fxml";

    // Derived paths - audio
    public static final String APP_LOGIN_SOUND = ASSETS_AUDIOCLIPS + "/app-login-sound.mp3";
    public static final String APP_CLOSE_SOUND = ASSETS_AUDIOCLIPS + "/app-close-sound.mp3";
    public static final String DASHBOARD_OPTION_CLICK_SOUND = ASSETS_AUDIOCLIPS + "/dashboard-option-click-sound.wav";
}
