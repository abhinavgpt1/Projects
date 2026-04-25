/**
 * Sample Skeleton for 'PaymentCollectionView.fxml' Controller Class
 */
// When payment is received; so must be updated and a msg needs to be sent monthly to the customer
package com.example.milkman_legacy.paymentCollection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;

public class PaymentCollectionController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="comboName"
	private ComboBox<String> comboName; // Value injected by FXMLLoader

	@FXML // fx:id="lblCq"
	private Label lblCq; // Value injected by FXMLLoader

	@FXML // fx:id="lblBq"
	private Label lblBq; // Value injected by FXMLLoader

	@FXML // fx:id="lblAmt"
	private Label lblAmt; // Value injected by FXMLLoader

	@FXML // fx:id="lblDtF"
	private Label lblDtF; // Value injected by FXMLLoader

	@FXML // fx:id="lblDtT"
	private Label lblDtT; // Value injected by FXMLLoader

	Connection con;

	@FXML
	void doReceive(ActionEvent event) {
		String name = comboName.getSelectionModel().getSelectedItem();
		try {
			if (name != null) {
				// updating unpaid bill to paid
				PreparedStatement pst = con.prepareStatement("update billpanel set status = ? where sname=? and doe=?");
				pst.setBoolean(1, true);
				pst.setString(2, name);
				pst.setDate(3, java.sql.Date.valueOf(LocalDate.parse(lblDtT.getText())));
				int count = pst.executeUpdate();
				// System.out.println(count);

				// removing the name from the list
				comboName.getItems().remove(name);
				// REMEMBER below record gets fired
				lblCq.setText("");
				lblBq.setText("");
				lblAmt.setText("");
				lblDtF.setText("");
				lblDtT.setText("");
			} else
				showMsg("Select Name");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void showMsg(String msg) {
		Alert al = new Alert(AlertType.ERROR);
		al.setTitle("Error");
		al.setContentText(msg);
		al.show();
	}

	@FXML
	void doFetch(ActionEvent event) {
		String name = comboName.getSelectionModel().getSelectedItem();
		try {
			// fetching all unpaid bills
			PreparedStatement pst = con.prepareStatement("select * from billpanel where sname=? and status=?");
			pst.setString(1, name);
			pst.setBoolean(2, false);
			ResultSet table = pst.executeQuery();
			while (table.next()) {
				lblCq.setText(String.valueOf(table.getFloat("cqty")));
				lblBq.setText(String.valueOf(table.getFloat("bqty")));
				lblAmt.setText(String.valueOf(table.getFloat("amount")));
				lblDtF.setText(table.getString("dos"));
				lblDtT.setText(table.getString("doe"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void fillCombo() {
		try {
			PreparedStatement pst = con.prepareStatement("select distinct sname from billpanel where status = ?");
			pst.setBoolean(1, false);
			ResultSet table = pst.executeQuery();
			while (table.next()) {
				comboName.getItems().add(table.getString("sname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
		fillCombo();
	}
}