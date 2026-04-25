/**
 * Sample Skeleton for 'VariationConsoleView.fxml' Controller Class
 */

package com.example.milkman_legacy.variationConsole;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import static com.example.milkman_legacy.util.UIHelper.isNumber;
import static com.example.milkman_legacy.util.UIHelper.showAlert;

/**
 * The purpose of this page is to store qty of milk delivered on a particular day which is different from the agreed upon qty and/or type during customer onboarding.
 * This variation is later utilized to calculate bill accordingly.
 */
public class VariationConsoleController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML // fx:id="listCust"
	private ListView<String> listCust; // Value injected by FXMLLoader

	@FXML // fx:id="txtCq"
	private TextField txtCq; // Value injected by FXMLLoader

	@FXML // fx:id="txtBq"
	private TextField txtBq; // Value injected by FXMLLoader

	@FXML // fx:id="dtpDate"
	private DatePicker dtpDate; // Value injected by FXMLLoader

	@FXML // fx:id="lblCq"
	private Label lblCq; // Value injected by FXMLLoader

	@FXML // fx:id="lblBq"
	private Label lblBq; // Value injected by FXMLLoader

	@FXML // fx:id="chkNil"
	private CheckBox chkNil; // Value injected by FXMLLoader

	@FXML // fx:id="imgCustomer"
	private ImageView imgCustomer; // Value injected by FXMLLoader

	@FXML // fx:id="imgNoFace"
	private ImageView imgNoFace; // Value injected by FXMLLoader

	private void reset() {
		imgCustomer.setVisible(false);
		imgNoFace.setVisible(true);
		resetAndFillOnboardedCustomers();
		lblBq.setText("");
		lblCq.setText("");
		txtBq.setText("");
		txtCq.setText("");
		chkNil.setSelected(false);
		dtpDate.setValue(LocalDate.now());
	}

	@FXML
	void doReset(ActionEvent event) {
		reset();
	}

	Connection con;

	@FXML
	void doDelete(ActionEvent event) {
		ObservableList<String> lstFull = listCust.getItems();
		ObservableList<String> lstSelected = listCust.getSelectionModel().getSelectedItems();
		lstFull.retainAll(lstSelected);
	}

	private boolean isDeliveryDateValid() {
		LocalDate deliveryDate = dtpDate.getValue();
		if (deliveryDate == null) {
			System.out.println("ERROR: Invalid date for variation save: " + dtpDate.getEditor().getText());
			showAlert("Invalid Date", "Enter a valid date in dd/mm/yyyy format", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isCustomCowMilkQtyValid() {
		String cowMilkQty = txtCq.getText();
		if(!isNumber(cowMilkQty) || Float.parseFloat(cowMilkQty) < 0) {
			System.out.println("ERROR: User tried saving with negative value for cow milk qty");
			showAlert("Invalid Cow Milk Qty", "Put a non-negative (zero or above) value in Cow Milk Qty", AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isCustomBuffaloMilkQtyValid() {
		String buffaloMilkQty = txtBq.getText();
		if(!isNumber(buffaloMilkQty) || Float.parseFloat(buffaloMilkQty) < 0) {
			System.out.println("ERROR: User tried saving with negative value for buffalo milk qty");
			showAlert("Invalid Buffalo Milk Qty", "Put a non-negative (zero or above) value in Buffalo Milk Qty", AlertType.ERROR);
			return false;
		}
		return true;
	}

	@FXML
	void doSave(ActionEvent event) {
		String sname = listCust.getSelectionModel().getSelectedItem();
		// no need of isBlank validation on sname since listView is readOnly, and sname are fetched from db itself.
		if (sname == null) {
			System.out.println("ERROR: Variation save attempted without selecting a customer");
			showAlert("Select Customer", "Select a customer before saving variation", AlertType.ERROR);
			return;
		}
		if (!isCustomCowMilkQtyValid() || !isCustomBuffaloMilkQtyValid() || !isDeliveryDateValid()) {
			System.out.println("ERROR: Validation failure before variation save");
			return;
		}
		float tmpCowMilkQty = Float.parseFloat(txtCq.getText());
		float fixedCowMilkQty = Float.parseFloat(lblCq.getText());
		float tmpBuffaloMilkQty = Float.parseFloat(txtBq.getText());
		float fixedBuffaloMilkQty = Float.parseFloat(lblBq.getText());

		float cowMilkQtyDifferential = 0;
		float buffaloMilkQtyDifferential = 0;
		if (chkNil.isSelected()) {
			// no validation required on lbl texts since they are coming from db and are read-only.
			// negative value is used to neutralize consumption of that particular day upon billing
			cowMilkQtyDifferential = -fixedCowMilkQty;
			buffaloMilkQtyDifferential = -fixedBuffaloMilkQty;
		} else {
			cowMilkQtyDifferential = tmpCowMilkQty - fixedCowMilkQty;
			buffaloMilkQtyDifferential = tmpBuffaloMilkQty - fixedBuffaloMilkQty;
		}

		try {
			PreparedStatement pst = con.prepareStatement("insert into variationconsole values(?,?,?,?)");
			pst.setString(1, sname);
			pst.setDate(2, java.sql.Date.valueOf(dtpDate.getValue()));
			pst.setFloat(3, cowMilkQtyDifferential);
			pst.setFloat(4, buffaloMilkQtyDifferential);

			int rowsAffected = pst.executeUpdate();
			if (rowsAffected == 1) {
				System.out.println("INFO: Variation stored for customer " + sname + " for date: " +  dtpDate.getValue() + ", cow: " + cowMilkQtyDifferential + ", buffalo: " + buffaloMilkQtyDifferential);
				showAlert("Variation Logged", "Variation logged for customer " + sname + " for date: " +  dtpDate.getValue() + ", cow: " + cowMilkQtyDifferential + ", buffalo: " + buffaloMilkQtyDifferential, AlertType.INFORMATION);
				listCust.getItems().remove(sname);
			} else {
				System.out.println("ERROR: Error in storing variation for customer  " + sname + " for date: " + dtpDate.getValue() + ", cow: " + cowMilkQtyDifferential + ", buffalo: " + buffaloMilkQtyDifferential);
				showAlert("Save Error", "Unknown error in saving variation for customer " + sname + " for date: " + dtpDate.getValue() + ", cow: " + cowMilkQtyDifferential + ", buffalo: " + buffaloMilkQtyDifferential + ". Please reach out to the team.", AlertType.ERROR);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void doDoubleClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			imgNoFace.setVisible(false);
			imgCustomer.setVisible(true);
			String name = listCust.getSelectionModel().getSelectedItem(); // since the list is read-only, there's no need to validate sname.
			try {
				PreparedStatement pst = con.prepareStatement("select cq,bq,imgpath from customerentry where sname=?");
				pst.setString(1, name);
				ResultSet table = pst.executeQuery();
				if (!table.next()) {
					System.out.println("ERROR: (Rare) Customer deleted from backend after retrieval");
					showAlert("Invalid Customer", "Looks like the customer record got deleted from backend. It's a rare event, check with team", AlertType.ERROR);
					return;
				}
				// default subscription values are filled in. lblCq, lblBq are for reference only.
				lblCq.setText(String.valueOf(table.getFloat("cq")));
				lblBq.setText(String.valueOf(table.getFloat("bq")));
				txtCq.setText(String.valueOf(table.getFloat("cq")));
			 	txtBq.setText(String.valueOf(table.getFloat("bq")));

				String path = table.getString("imgpath"); // URI.toString()
				if (path.equals("nil")) {
					imgCustomer.setVisible(false);
					imgNoFace.setVisible(true);
				}
				else {
					imgNoFace.setVisible(false);
					imgCustomer.setVisible(true);
					imgCustomer.setImage(new Image(path));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void resetAndFillOnboardedCustomers() {
		listCust.getItems().clear();
		ArrayList<String> ary = new ArrayList<>();
		try {
			PreparedStatement pst = con.prepareStatement("select sname from customerentry");
			ResultSet table = pst.executeQuery();
			while (table.next()) {
				String name = table.getString("sname");
				ary.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		listCust.getItems().addAll(ary);
	}

	@FXML
	void initialize() {
		// TODO: use single imageView and render default or customer photo at runtime. Don't maintain 2 imageView.
		// TODO: on NIL selection, txtCq and txtBq should populate 0.
		con = DBConnection.doConnect();
		listCust.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // this is purely from the pov of deleting customers which don't have a variation in milk delivered on a particular day.
		reset();
	}
}