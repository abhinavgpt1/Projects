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

public class DashboardController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

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

    URL url;
    AudioClip audio;

    void playSound(String snd) {
        url = getClass().getResource(snd);
        audio = new AudioClip(url.toString());
        audio.play();
    }

    void openFile(String s) {
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

    @FXML
    void openAllCustomers(MouseEvent event) {
        String s = "allcustomers/allcustomersView.fxml";
        openFile(s);
    }

    @FXML
    void openBillPanel(MouseEvent event) {
        String s = "billpanel/billpanelView.fxml";
        openFile(s);
    }

    @FXML
    void openBillingHistory(MouseEvent event) {
        String s = "billinghistory/billinghistoryView.fxml";
        openFile(s);
    }

    @FXML
    void openCustomerEntry(MouseEvent event) {
        String s = "customerentry/customerentryView.fxml";
        openFile(s);
    }

    @FXML
    void openIncomeRecord(MouseEvent event) {
        String s = "incomerecord/incomerecordView.fxml";
        openFile(s);
    }

    @FXML
    void openPaymentCollection(MouseEvent event) {
        String s = "paymentcollection/paymentcollectionView.fxml";
        openFile(s);
    }

    @FXML
    void openVariationConsole(MouseEvent event) {
        String s = "variationconsole/variationconsoleView.fxml";
        openFile(s);
    }

    @FXML
    void openVariationDisplay(MouseEvent event) {
        String s = "variationdisplay/variationdisplayView.fxml";
        openFile(s);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

    }
}