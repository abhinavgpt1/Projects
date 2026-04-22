package com.example.milkman_legacy;

public class Constants {
    // Basic paths
    private static final String ROOT_PATH = "/com/example/milkman_legacy";
    private static final String ASSETS = ROOT_PATH + "/assets";
    private static final String ASSETS_AUDIOCLIPS = ASSETS + "/audioclips";
    private static final String LOGIN = ROOT_PATH + "/login";
    private static final String DASHBOARD = ROOT_PATH + "/dashboard";
    private static final String ALL_CUSTOMERS = ROOT_PATH + "/allCustomers";
    private static final String BILL_PANEL = ROOT_PATH + "/billPanel";
    private static final String BILLING_HISTORY = ROOT_PATH + "/billingHistory";
    private static final String CUSTOMER_ENTRY = ROOT_PATH + "/customerEntry";
    private static final String INCOME_RECORD = ROOT_PATH + "/incomeRecord";
    private static final String PAYMENT_COLLECTION = ROOT_PATH + "/paymentCollection";
    private static final String VARIATION_CONSOLE = ROOT_PATH + "/variationConsole";
    private static final String VARIATION_DISPLAY = ROOT_PATH + "/variationDisplay";

    // Derived paths - fxml
    public static final String ALL_CUSTOMERS_VIEW = ALL_CUSTOMERS + "/AllCustomersView.fxml";
    public static final String BILLING_HISTORY_VIEW = BILLING_HISTORY + "/BillingHistoryView.fxml";
    public static final String BILL_PANEL_VIEW = BILL_PANEL + "/BillPanelView.fxml";
    public static final String CUSTOMER_ENTRY_VIEW = CUSTOMER_ENTRY + "/CustomerEntryView.fxml";
    public static final String DASHBOARD_VIEW = DASHBOARD + "/DashboardView.fxml";
    public static final String INCOME_RECORD_VIEW = INCOME_RECORD + "/IncomeRecordView.fxml";
    public static final String LOGIN_VIEW = LOGIN + "/LoginView.fxml";
    public static final String PAYMENT_COLLECTION_VIEW = PAYMENT_COLLECTION + "/PaymentCollectionView.fxml";
    public static final String VARIATION_CONSOLE_VIEW = VARIATION_CONSOLE + "/VariationConsoleView.fxml";
    public static final String VARIATION_DISPLAY_VIEW = VARIATION_DISPLAY + "/VariationDisplayView.fxml";

    // Derived paths - audio
    public static final String APP_LOGIN_SOUND = ASSETS_AUDIOCLIPS + "/app-login-sound.mp3";
    public static final String APP_CLOSE_SOUND = ASSETS_AUDIOCLIPS + "/app-close-sound.mp3";
    public static final String DASHBOARD_OPTION_CLICK_SOUND = ASSETS_AUDIOCLIPS + "/dashboard-option-click-sound.wav";
}