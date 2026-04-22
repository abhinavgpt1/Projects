/**
 * Sample Skeleton for 'DashboardView.fxml' Controller Class
 */

package com.example.milkman_legacy.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import static com.example.milkman_legacy.Constants.APP_CLOSE_SOUND;
import static com.example.milkman_legacy.Constants.DASHBOARD_OPTION_CLICK_SOUND;
import static com.example.milkman_legacy.Constants.ALL_CUSTOMERS_VIEW;
import static com.example.milkman_legacy.Constants.BILL_PANEL_VIEW;
import static com.example.milkman_legacy.Constants.BILLING_HISTORY_VIEW;
import static com.example.milkman_legacy.Constants.CUSTOMER_ENTRY_VIEW;
import static com.example.milkman_legacy.Constants.INCOME_RECORD_VIEW;
import static com.example.milkman_legacy.Constants.PAYMENT_COLLECTION_VIEW;
import static com.example.milkman_legacy.Constants.VARIATION_CONSOLE_VIEW;
import static com.example.milkman_legacy.Constants.VARIATION_DISPLAY_VIEW;

public class DashboardController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    // -----------------------helper methods-----------------------
    private void playSound(String snd) {
        URL url = getClass().getResource(snd);
        AudioClip audio = new AudioClip(url.toString());
        audio.play();
    }

    private void openView(String s) {
        try {
            playSound(DASHBOARD_OPTION_CLICK_SOUND);
            Parent root = FXMLLoader.load(getClass().getResource(s));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------public methods-----------------------
    @FXML
    void doClose(ActionEvent event) {
        playSound(APP_CLOSE_SOUND);
        try {
            // to hear a sound while exiting
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @FXML
    void openAllCustomers(MouseEvent event) {
        openView(ALL_CUSTOMERS_VIEW);
    }

    @FXML
    void openBillPanel(MouseEvent event) {
        openView(BILL_PANEL_VIEW);
    }

    @FXML
    void openBillingHistory(MouseEvent event) {
        openView(BILLING_HISTORY_VIEW);
    }

    @FXML
    void openCustomerEntry(MouseEvent event) {
        openView(CUSTOMER_ENTRY_VIEW);
    }

    @FXML
    void openIncomeRecord(MouseEvent event) {
        openView(INCOME_RECORD_VIEW);
    }

    @FXML
    void openPaymentCollection(MouseEvent event) {
        openView(PAYMENT_COLLECTION_VIEW);
    }

    @FXML
    void openVariationConsole(MouseEvent event) {
        openView(VARIATION_CONSOLE_VIEW);
    }

    @FXML
    void openVariationDisplay(MouseEvent event) {
        openView(VARIATION_DISPLAY_VIEW);
    }

    @FXML
    void initialize() {
        // This method is called by the FXMLLoader when initialization is complete
    }
}