/**
 * Sample Skeleton for 'IncomeRecordView.fxml' Controller Class
 */

package com.example.milkman_legacy.incomeRecord;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class IncomeRecordController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="dtpDoF"
    private DatePicker dtpDoF; // Value injected by FXMLLoader

    @FXML // fx:id="dtpDoT"
    private DatePicker dtpDoT; // Value injected by FXMLLoader

    @FXML // fx:id="lblAmt"
    private Label lblAmt; // Value injected by FXMLLoader

    Connection con;

    @FXML
    void doTotal(ActionEvent event) {
        try {
            PreparedStatement pst = con.prepareStatement("select sum(amount) from billpanel where status=true and dos>=? and doe<=?");
            pst.setDate(1, java.sql.Date.valueOf(dtpDoF.getValue()));
            pst.setDate(2, java.sql.Date.valueOf(dtpDoT.getValue()));
            ResultSet table = pst.executeQuery();
            table.next();
            lblAmt.setText(String.valueOf(table.getFloat("sum(amount)"))); // 0 if no records applicable (includes empty table case too)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        con = DBConnection.doConnect();
    }
}