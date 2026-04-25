/**
 * Sample Skeleton for 'BillPanelView.fxml' Controller Class
 */

package com.example.milkman_legacy.billPanel;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import com.example.milkman_legacy.sms.SST_SMS;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import static com.example.milkman_legacy.util.UIHelper.showAlert;

public class BillPanelController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="dtpDos"
	private DatePicker dtpDos; // Value injected by FXMLLoader

	@FXML // fx:id="imgNoFace"
	private ImageView imgNoFace; // Value injected by FXMLLoader

	@FXML // fx:id="listCust"
	private ListView<String> listCust; // Value injected by FXMLLoader

	@FXML // fx:id="dtpDoe"
	private DatePicker dtpDoe; // Value injected by FXMLLoader

	@FXML // fx:id="lblDays"
	private Label lblDays; // Value injected by FXMLLoader

	@FXML // fx:id="lblBill"
	private Label lblBill; // Value injected by FXMLLoader
	@FXML // fx:id="lblCq"
	private Label lblCq; // Value injected by FXMLLoader

	@FXML // fx:id="lblVBq"
	private Label lblVBq; // Value injected by FXMLLoader

	@FXML // fx:id="lblVCq"
	private Label lblVCq; // Value injected by FXMLLoader

	@FXML // fx:id="lblBp"
	private Label lblBp; // Value injected by FXMLLoader

	@FXML // fx:id="lblCp"
	private Label lblCp; // Value injected by FXMLLoader

	@FXML // fx:id="lblBq"
	private Label lblBq; // Value injected by FXMLLoader

	@FXML // fx:id="imgCust"
	private ImageView imgCust; // Value injected by FXMLLoader

	Connection con;
	private Date dateOfSubscriptionStart = null;
	private boolean isPendingBill = false;
	private Date customerLastBillEndDate = null;
	private float netCowMilkQtyPurchased = 0;
	private float netBuffaloMilkQtyPurchased = 0;

	private boolean isNameSelected() {
		String selectedName = listCust.getSelectionModel().getSelectedItem();
		if(selectedName == null || selectedName.isBlank()) {
			System.out.println("ERROR: Name not selected: " + selectedName);
			showAlert("Select Name", "Select customer from list to proceed further", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isDateOfStartValid() {
		if (dtpDos.getValue() == null) {
			System.out.println("ERROR: Invalid date of start: " + dtpDos.getEditor().getText());
			showAlert("Invalid Date of Start", "Enter a valid date in dd/mm/yyyy format", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isDateOfEndValid() {
		if (dtpDoe.getValue() == null) {
			System.out.println("ERROR: Invalid date of end: " + dtpDoe.getEditor().getText());
			showAlert("Invalid Date of End", "Enter a valid date in dd/mm/yyyy format", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isDateOfStartBeforeOrEqualsDateOfEnd() {
		if (dtpDos.getValue().isAfter(dtpDoe.getValue())) {
			String errorMsg = String.format("Date of Start (%s) is greater than Date of End (%s)", dtpDos.getValue(), dtpDoe.getValue());
			System.out.println("ERROR: " + errorMsg);
			showAlert("Invalid Date Range", errorMsg, Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isDateOfStartAfterOrEqualsDateOfSubscription() {
		// Rule: during onboarding, dateOfSubscriptionStart is set. So, it can't be invalid.
		if(dtpDos.getValue().isBefore(dateOfSubscriptionStart.toLocalDate())) {
			String errorMsg = String.format("Date of start (%s) can't be before customer's date of subscription start (%s). This will cause misleading bills", dtpDos.getValue(), dateOfSubscriptionStart);
			System.out.println("ERROR: " + errorMsg);
			showAlert("Invalid Date of Start", errorMsg, AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isDateOfStartAfterCustomerLastBillEndDate() {
		// Do not generate bill over a range which contains last bill's end date to avoid overlap
		if (customerLastBillEndDate == null || dtpDos.getValue().isAfter(customerLastBillEndDate.toLocalDate()))
			return true;
		String errorMsg = String.format("Date of start (%s) can't be before/equal to customer's last bill date (%s). This will cause overlapping bills", dtpDos.getValue(), customerLastBillEndDate);
		System.out.println("ERROR: " + errorMsg);
		showAlert("Invalid Date of Start", errorMsg, AlertType.ERROR);
		return false;
	}

	private boolean isValidDateRange() {
		return isDateOfStartValid() && isDateOfEndValid() && isDateOfStartBeforeOrEqualsDateOfEnd()
				&& isDateOfStartAfterOrEqualsDateOfSubscription() && isDateOfStartAfterCustomerLastBillEndDate();
	}

	private boolean arePendingBillsCleared() {
		if (!isPendingBill) {
			return true;
		}
		String sname = listCust.getSelectionModel().getSelectedItem(); // assuming name would be validated before this validation. If not, nothing break otherwise.
		String errorMsg = String.format("Clear pending bills for %s before this operation", sname);
		System.out.println("ERROR: " + errorMsg);
		showAlert("Invalid Operation", errorMsg, AlertType.ERROR);
		return false;
	}

	@FXML
	boolean doGetVariation(ActionEvent event) {
		// Rule: If customer has any unpaid bill, then getVariation isn't done => generateBill and save&Sms shouldn't be done either.
		// TODO: Ideally this shouldn't be the case. We should save the new bill, and send sms to clear pending bills.

		if(!isNameSelected() || !isValidDateRange() || !arePendingBillsCleared()) {
			return false;
		}
		String sname = listCust.getSelectionModel().getSelectedItem();
		try {
			// Rule: Start Date and End Date for a bill are inclusive.
			PreparedStatement pst = con.prepareStatement("select sum(cq), sum(bq) from variationconsole where sname=? and cdate>=? and cdate<=?");
			pst.setString(1, sname);
			pst.setDate(2, Date.valueOf(dtpDos.getValue()));
			pst.setDate(3, Date.valueOf(dtpDoe.getValue()));
			ResultSet table = pst.executeQuery();
			if (table.next()) {
				lblVCq.setText(String.valueOf(table.getFloat("sum(cq)")));
				lblVBq.setText(String.valueOf(table.getFloat("sum(bq)")));
			} else {
				String errorMsg = "Persistence error in fetching customer's variation details: " + sname + " for dates: (" + dtpDos.getValue() + ", " + dtpDoe.getValue() + ")";
				System.out.println("ERROR: " + errorMsg);
				showAlert("Database Error", errorMsg + ", please reach out to the team.", AlertType.ERROR);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	@FXML
	void doDoubleClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			// Get selected customer's contracted consumption values
			String sname = listCust.getSelectionModel().getSelectedItem(); // no need for validation on name since listCust is read-only and sname are coming directly from db.
			try {
				PreparedStatement pst = con.prepareStatement("select cq,cprice,bq,bprice,dos,imgpath from customerentry where sname=?");
				pst.setString(1, sname);
				ResultSet table = pst.executeQuery();
				if(table.next()) {
					lblCq.setText(String.valueOf(table.getFloat("cq")));
					lblCp.setText(String.valueOf(table.getFloat("cprice")));
					lblBq.setText(String.valueOf(table.getFloat("bq")));
					lblBp.setText(String.valueOf(table.getFloat("bprice")));
					dateOfSubscriptionStart = table.getDate("dos"); // saved for later
					dtpDos.setValue(dateOfSubscriptionStart.toLocalDate());
					String path = table.getString("imgpath");
					if (path.equals("nil")) {
						imgCust.setVisible(false);
						imgNoFace.setVisible(true);
					} else {
						imgNoFace.setVisible(false);
						imgCust.setImage(new Image(path));
						imgCust.setVisible(true);
					}
				} else {
					String errorMsg = "Persistence issue while fetching customer's consumption details: " + sname;
					System.out.println("ERROR: " + errorMsg);
					showAlert("Database Error", errorMsg + ", please reach out to the team.", AlertType.ERROR);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				PreparedStatement pst = con.prepareStatement("select min(status) as minStatus, max(doe) as customerLastBillEndDate from billpanel where sname = ?");
				pst.setString(1, sname);
				ResultSet table = pst.executeQuery();
				if (table.next()) {
					// IMP: getBoolean() for null gives false by default. getString is always safe.
					String minStatus = table.getString("minStatus");
					if (minStatus != null && minStatus == "0") {
						isPendingBill = true;
					}
					customerLastBillEndDate = table.getDate("customerLastBillEndDate");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// fyi, dateOfSubscriptionStart and customerLastBillEndDate are by default null. isPendingBill is by default false.
		}
	}

	@FXML
	boolean doBill(ActionEvent event) {
		if(!doGetVariation(event)) {
			System.out.println("ERROR: Get Variation failed");
			return false;
		}

		// Bill calculation
		long days = ChronoUnit.DAYS.between(dtpDos.getValue(), dtpDoe.getValue()) + 1; // DOE should be inclusive
		float fixedCowMilkQty = Float.parseFloat(lblCq.getText());
		float variationCowMilkQty = Float.parseFloat(lblVCq.getText());
		float fixedCowMilkPrice = Float.parseFloat(lblCp.getText());
		netCowMilkQtyPurchased = (fixedCowMilkQty * days + variationCowMilkQty); // saved for later
		float cowMilkBill = netCowMilkQtyPurchased * fixedCowMilkPrice;

		float fixedBuffaloMilkQty = Float.parseFloat(lblBq.getText());
		float variationBuffaloMilkQty = Float.parseFloat(lblVBq.getText());
		float fixedBuffaloMilkPrice = Float.parseFloat(lblBp.getText());
		netBuffaloMilkQtyPurchased = (fixedBuffaloMilkQty * days + variationBuffaloMilkQty);
		float buffaloMilkBill = netBuffaloMilkQtyPurchased * fixedBuffaloMilkPrice; // saved for later

		float totalBill = cowMilkBill + buffaloMilkBill;
		lblDays.setText(String.valueOf(days)); // TODO: this should be set when DOE is selected
		lblBill.setText(String.valueOf(totalBill));
		return true;
		// TODO: what if a user who's not a buffalo milk consumer, someday wants to purchase it? You should have provision for price in Variation console
		// TODO: Bug: if a customer who doesn't purchase buffalo milk, has a variation of buffalo milk, then since BuffaloMilkPrice = 0 (in contract), final bill won't be affected it.
	}

	@FXML
	void doSaveAndSms(ActionEvent event) {
		if(!doBill(event)) {
			System.out.println("ERROR: Bill generation failed");
			return;
		}

		String sname = listCust.getSelectionModel().getSelectedItem();
		try {
			PreparedStatement pst = con.prepareStatement("insert into billpanel values(?,?,?,?,?,?,?)");
			pst.setString(1, sname);
			pst.setDate(2, java.sql.Date.valueOf(dtpDos.getValue()));
			pst.setDate(3, java.sql.Date.valueOf(dtpDoe.getValue()));
			pst.setFloat(4, Float.parseFloat(lblBill.getText()));
			pst.setFloat(5, netCowMilkQtyPurchased);
			pst.setFloat(6, netBuffaloMilkQtyPurchased);
			pst.setBoolean(7, false);
			int rowsAffected = pst.executeUpdate();
			if (rowsAffected == 1) {
				String infoMessage = String.format("Bill saved for customer (%s) for dates (%s, %s)", sname, dtpDos.getValue(), dtpDoe.getValue());
				System.out.println("INFO: " + infoMessage);
				// showAlert("Bill Saved", infoMessage , AlertType.INFORMATION); // can skip since we have alert on sms success.
			} else {
				String errorMsg = String.format("Error in storing bill for customer (%s) for dates (%s, %s)", sname, dtpDos.getValue(), dtpDoe.getValue());
				System.out.println("ERROR: " + errorMsg);
				showAlert("Save Error", errorMsg + ", please reach out to the team.", AlertType.ERROR);
				return;
			}
			// remove the person from list once bill is saved (for month, generally).
			// TODO: Add ResetAll to help put another bill for customer. How about multi-select bulk bill generation?
			listCust.getItems().remove(sname);

			// ***************SMS***********
			// TODO: other mediums of notifications can be used here. This query can be removed if mobileNum is stored on doDoubleClick()
			// TODO: Bug: Mobile isn't a required field for customer, so following code can fail.
			pst = con.prepareStatement("select mobile from customerentry where sname=?");
			pst.setString(1, sname);
			ResultSet table = pst.executeQuery();
			if (table.next()) {
				String mobileNumber = table.getString("mobile");
				final String warningMessage = "KINDLY, PAY IT ON TIME TO AVOID SUBSCRIPTION CANCELLATION AND PENALTY.";
				String mobileMessage = String.format("Your bill for date %s to %s is pending.\n%s", dtpDos.getValue(), dtpDoe.getValue(), warningMessage);

				String resp = SST_SMS.bceSunSoftSend(mobileNumber, mobileMessage);
				if (resp.contains("Exception")) {
					String errorMsg = String.format("Failed to sent SMS for customer (%s) for bill date (%s, %s). Contact the service provider, or check the internet connection and try again", sname, dtpDos.getValue(), dtpDoe.getValue());
					System.out.println("ERROR: " + errorMsg);
					showAlert("SMS Failed", errorMsg, AlertType.ERROR);
				} else {
					String infoMessage = String.format("Bill saved and sent to customer (%s) for dates (%s, %s)", sname, dtpDos.getValue(), dtpDoe.getValue());
					System.out.println("INFO: " + infoMessage);
					showAlert("Bill Sent", infoMessage, AlertType.INFORMATION);
				}
			} else {
				String errorMsg = "Persistence error in fetching customer's mobile number: " + sname;
				System.out.println("ERROR: " + errorMsg);
				showAlert("Database Error", errorMsg + ", please reach out to the team.", AlertType.ERROR);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void fillOnboardedCustomers() {
		ArrayList<String> customers = new ArrayList<>();
		try {
			PreparedStatement pst = con.prepareStatement("select sname from customerentry");
			ResultSet table = pst.executeQuery();
			while (table.next()) {
				customers.add(table.getString("sname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		listCust.getItems().addAll(customers);
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
		imgNoFace.setVisible(true);
		fillOnboardedCustomers();
		lblBp.setText("0");
		lblCp.setText("0");
		lblCq.setText("0");
		lblBq.setText("0");
		lblVBq.setText("0");
		lblVCq.setText("0");
	}
}