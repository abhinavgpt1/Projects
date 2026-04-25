/**
 * Sample Skeleton for 'PaymentCollectionView.fxml' Controller Class
 */
// When payment is received; so must be updated and a msg needs to be sent monthly to the customer
package com.example.milkman_legacy.paymentCollection;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;

import static com.example.milkman_legacy.util.UIHelper.showAlert;

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
	private Date pendingBillStartDate = null;
	private Date pendingBillEndDate = null;

	private boolean isNameSelected() {
		String selectedName = comboName.getSelectionModel().getSelectedItem();
		if(selectedName == null || selectedName.isBlank()) {
			System.out.println("ERROR: Customer not selected from list: " + selectedName);
			showAlert("Select Customer", "Select a customer from list to record payment", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private void resetLabels() {
		lblCq.setText("");
		lblBq.setText("");
		lblAmt.setText("");
		lblDtF.setText("");
		lblDtT.setText("");
	}

	@FXML
	void doReceive(ActionEvent event) {
		// Say you fetch customer details. Now you selected another customer from dropdown but didn't clicked "Fetch",
		// then without doFetch(), you would update this customer's bill for last customer's bill's dos & doe
		if (!doFetch(event)) {
			System.out.println("ERROR: Fetch bill details failed");
			return;
		}
		String sname = comboName.getSelectionModel().getSelectedItem();
		String query = "update billpanel set status = true where sname=? and dos=? and doe=?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			// Update unpaid bill to Paid
			pst.setString(1, sname);
			pst.setDate(2, pendingBillStartDate);
			pst.setDate(3, pendingBillEndDate);
			int rowsAffected = pst.executeUpdate();
			if (rowsAffected == 1) {
				String infoMessage = String.format("Bill updated to paid for customer (%s) for dates (%s, %s)", sname, pendingBillStartDate, pendingBillEndDate);
				System.out.println("INFO: " + infoMessage);
				 showAlert("Recorded Payment", infoMessage , AlertType.INFORMATION);
			} else {
				String errorMsg = String.format("Error in updating bill to paid for customer (%s) for dates (%s, %s)", sname, pendingBillStartDate, pendingBillEndDate);
				System.out.println("ERROR: " + errorMsg);
				showAlert("Unknown Error", errorMsg + ", please reach out to the team.", AlertType.ERROR);
				return;
			}
			// removing the name from the list => comboName.getSelectionModel().getSelectedItem() returns empty now. Hence, no resetting required here.
			comboName.getItems().remove(sname);

			resetLabels();
			// NOTE: Glitch found: Just after doReceive() if I select another customer and don't click on doFetch(),
			// then above mentioned doFetch() populated latest fields in labels but not on UI. If resetLabels() didn't come in action then we would see them.
			// So, all in all, even if you don't click Fetch, behind the scene correct name/dos/doe are used to update bill. It's juts that user might not see the details in this scenario.
			// tldr; use Fetch button to see what details you're updating, otherwise backend is robust enough to perform right operation.
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	boolean doFetch(ActionEvent event) {
		if (!isNameSelected()) {
			return false;
		}

		String sname = comboName.getSelectionModel().getSelectedItem();
		try {
			// Fetching all unpaid bills of this customer
			// TODO: what if there are multiple unpaid bills? Following code would keep on overwriting.
			PreparedStatement pst = con.prepareStatement("select * from billpanel where sname=? and status=false");
			pst.setString(1, sname);
			ResultSet table = pst.executeQuery();
			while (table.next()) {
				lblCq.setText(String.valueOf(table.getFloat("cqty")));
				lblBq.setText(String.valueOf(table.getFloat("bqty")));
				pendingBillStartDate = table.getDate("dos");
				lblDtF.setText(pendingBillStartDate.toString()); // saving for later reference in doReceive()
				pendingBillEndDate = table.getDate("doe");
				lblDtT.setText(pendingBillEndDate.toString()); // saving for later reference in doReceive()
				lblAmt.setText(String.valueOf(table.getFloat("amount")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void fillCustomersWithPendingBills() {
		String query = "select distinct sname from billpanel where status = false";
		try (PreparedStatement pst = con.prepareStatement(query);
			 ResultSet table = pst.executeQuery()) {
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
		fillCustomersWithPendingBills();
		resetLabels();
	}
}