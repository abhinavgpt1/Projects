/**
 * Sample Skeleton for 'CustomerEntryView.fxml' Controller Class
 */

package com.example.milkman_legacy.customerEntry;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.example.milkman_legacy.dbutil.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class CustomerEntryController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="comboCust"
	private ComboBox<String> comboCust; // Value injected by FXMLLoader

	@FXML // fx:id="txtMob"
	private TextField txtMob; // Value injected by FXMLLoader

	@FXML // fx:id="txtAddress"
	private TextField txtAddress; // Value injected by FXMLLoader

	@FXML // fx:id="imgCustomer"
	private ImageView imgCustomer; // Value injected by FXMLLoader

	@FXML // fx:id="txtCq"
	private TextField txtCq; // Value injected by FXMLLoader

	@FXML // fx:id="txtBp"
	private TextField txtBp; // Value injected by FXMLLoader

	@FXML // fx:id="txtCp"
	private TextField txtCp; // Value injected by FXMLLoader

	@FXML // fx:id="txtBq"
	private TextField txtBq; // Value injected by FXMLLoader

	@FXML // fx:id="dtpDos"
	private DatePicker dtpDos; // Value injected by FXMLLoader

	@FXML // fx:id="imgNoFace"
	private ImageView imgNoFace; // Value injected by FXMLLoader

	String imgPath = "nil";
	Connection con;

	private String getDefaultProfilePhotoSelectionPath() {
		// Desktop
		String osName = System.getProperty("os.name").toLowerCase();
		String defaultProfilePhotoSelectionPath;
		if (osName.contains("win")) {
			defaultProfilePhotoSelectionPath = System.getProperty("user.home") + "\\Desktop";
		} else {
			defaultProfilePhotoSelectionPath = System.getProperty("user.home");
		}
		return defaultProfilePhotoSelectionPath;
	}

	@FXML
	void doBrowse(ActionEvent event) {
		// Selecting an image
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Select Profile Photo");
		filechooser.setInitialDirectory(new File(getDefaultProfilePhotoSelectionPath()));
		// For fixed paths in Windows, use \\ for separation
		filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
		// folders will be displayed along with images of formats mentioned

		File selectedfile = filechooser.showOpenDialog(null);
		if (selectedfile != null) {
			// URI.toString returns file:/C:/Users/... (file:/ is ambiguous if new File("file:/..."))
			// FYI, File expects a System File Path, but toString() gives it a Network-style URL.
			// Make sure to retrieve File path correctly
			imgNoFace.setVisible(false);
			imgPath = selectedfile.toURI().toString();
			imgCustomer.setImage(new Image(imgPath));
			// TOOD: onSave, save this image into your server/assets for retrieval. Think outside localhost.
		}
	}

	@FXML
	void doDelete(ActionEvent event) {
		imgCustomer.setVisible(false);
		imgNoFace.setVisible(true);
		String name = comboCust.getSelectionModel().getSelectedItem();
		if (!isCustomerNameValid()) {
			System.out.println("ERROR: Delete triggered for invalid name: " + name);
			return;
		}
		if (!doesCustomerExist(name)) {
			System.out.println("WARNING: Delete attempted on customer which doesn't exist in database: " + name);
			showAlert("Invalid Customer", "This customer " + name + " doesn't exist in our database. Please use another one to delete.", Alert.AlertType.WARNING);
			return;
		}
		try {
			PreparedStatement pst = con.prepareStatement("delete from customerentry where sname=?");
			pst.setString(1, name);
			int rowsAffected = pst.executeUpdate();
			if(rowsAffected == 1) {
				System.out.println("INFO: Customer deleted: " + name);
				showAlert("Delete Successful", "Customer deleted successfully", Alert.AlertType.INFORMATION);
			} else {
				System.out.println("ERROR: Delete failed for " + name);
				showAlert("Delete Failed", "Delete failed for customer " + name + ". Please reach out to team.", Alert.AlertType.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		doNew(event);
	}

	@FXML
	void doFetch(ActionEvent event) {
		String name = comboCust.getSelectionModel().getSelectedItem();
		if (isCustomerNameValid()) {
			try {
				PreparedStatement pst = con.prepareStatement("select * from customerentry where sname=?");
				pst.setString(1, name);
				ResultSet table = pst.executeQuery();
				while(table.next()) {
					txtMob.setText(table.getString("mobile"));
					txtAddress.setText(table.getString("address"));
					txtCq.setText(String.valueOf(table.getFloat("cq")));
					txtCp.setText(String.valueOf(table.getFloat("cprice")));
					txtBq.setText(String.valueOf(table.getFloat("bq")));
					txtBp.setText(String.valueOf(table.getFloat("bprice")));
					dtpDos.setValue(table.getDate("dos").toLocalDate());
					String imageUriPath = table.getString("imgpath");
					if (imageUriPath.equals("nil"))
						imgNoFace.setVisible(true);
					else {
						imgNoFace.setVisible(false);
						imgCustomer.setImage(new Image(imageUriPath.toString()));
						imgCustomer.setVisible(true);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			imgCustomer.setVisible(false);
			imgNoFace.setVisible(true);
		}
	}

	@FXML
	void doNew(ActionEvent event) {
		imgCustomer.setImage(null);
		imgCustomer.setVisible(false);
		imgNoFace.setVisible(true);
		txtAddress.setText("");
		txtMob.setText("");
		txtBp.setText("0");
		txtBq.setText("0");
		txtCp.setText("0");
		txtCq.setText("0");
		dtpDos.setValue(LocalDate.now());
	}

	public boolean isNumber(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch(NumberFormatException | NullPointerException n){
			System.out.println(value + " isn't a number");
			return false;
		}
	}

	public void showAlert(String title, String message, Alert.AlertType alertType){
		// TODO: wrap long texts in Alert
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.show();
	}
	private boolean isCustomerNameValid() {
		String sname = comboCust.getSelectionModel().getSelectedItem();
		if (sname == null || sname.isBlank()) {
			showAlert("Invalid Name", "Name missing. Use alphabets for naming a person.", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}
	private boolean isCowMilkQtyAndPriceValid() {
		String cowMilkQty = txtCq.getText();
		String cowMilkPrice = txtCp.getText();
		if (!isNumber(cowMilkQty) || !isNumber(cowMilkPrice)) {
			showAlert("Invalid Cow Milk Qty/Price", "Either Cow Milk Qty or Price is not a number. Enter a valid number.", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}
	private boolean isBuffaloMilkQtyAndPriceValid() {
		String buffaloMilkQty = txtBq.getText();
		String buffaloMilkPrice = txtBp.getText();
		if (!isNumber(buffaloMilkQty) || !isNumber(buffaloMilkPrice)) {
			showAlert("Invalid Buffalo Milk Qty/Price", "Either Buffalo Milk Qty or Price is not a number. Enter a valid number.", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}
	private boolean isDateOfMilkSubscriptionStartValid() {
		LocalDate dateOfMilkSubscriptionStart = dtpDos.getValue();
		if (dateOfMilkSubscriptionStart == null) {
			// no alert since we're saving default date as CURRENT_DATE
			// showAlert("Invalid Date", "Enter a valid date in dd/mm/yyyy format", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}
	private boolean validateCustomerEntryDetailsBeforeSave() {
		if (!isCustomerNameValid()) return false;
		if (!isCowMilkQtyAndPriceValid()) return false;
		if (!isBuffaloMilkQtyAndPriceValid()) return false;
		return true;
	}

	@FXML
	void doSave(ActionEvent event) {
		if (!validateCustomerEntryDetailsBeforeSave()) {
			System.out.println("ERROR: Validation failure before save");
			return;
		}
		String name = comboCust.getSelectionModel().getSelectedItem();
		try {
			PreparedStatement pst = con.prepareStatement("insert into customerentry values(?,?,?,?,?,?,?,?,?)");
			pst.setString(1, name);
			pst.setString(2, txtMob.getText());
			pst.setString(3, txtAddress.getText());
			pst.setFloat(4, Float.parseFloat(txtCq.getText()));
			pst.setFloat(5, Float.parseFloat(txtCp.getText()));
			pst.setFloat(6, Float.parseFloat(txtBq.getText()));
			pst.setFloat(7, Float.parseFloat(txtBp.getText()));
			LocalDate dateOfMilkSubscriptionStart = LocalDate.now();
			if (isDateOfMilkSubscriptionStartValid())
				dateOfMilkSubscriptionStart = dtpDos.getValue();
			pst.setDate(8, java.sql.Date.valueOf(dateOfMilkSubscriptionStart));
			pst.setString(9, imgPath);
			int rowsAffected = pst.executeUpdate();
			if (rowsAffected == 1) {
				System.out.println("INFO: Customer onboarded: " + name + ", " + dateOfMilkSubscriptionStart);
				showAlert("Customer Onboarded", "Milk subscription started for " + name + " from " + dateOfMilkSubscriptionStart, Alert.AlertType.INFORMATION);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean doesCustomerExist(String sname) {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select * from customerentry where sname=?");
        	pst.setString(1, sname);
			ResultSet table = pst.executeQuery();
			while(table.next()) {
				return true;
			}
			return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}
	@FXML
	void doUpdate(ActionEvent event) {
		String name = comboCust.getSelectionModel().getSelectedItem();
		if (!isCustomerNameValid()) {
			System.out.println("ERROR: Update triggered for invalid name: " + name);
			return;
		}
		if (!doesCustomerExist(name)) {
			System.out.println("WARNING: Update attempted on customer which doesn't exist in database: " + name);
			showAlert("Invalid Customer", "This customer " + name + " doesn't exist in our database. Please use another one to update.", Alert.AlertType.WARNING);
			return;
		}
		try {
			PreparedStatement pst = con.prepareStatement(
					"update customerentry set mobile=?,address=?,cq=?,cprice=?,bq=?,bprice=?,dos=?,imgpath=? where sname=?");
			pst.setString(9, name);
			pst.setString(1, txtMob.getText());
			pst.setString(2, txtAddress.getText());
			pst.setFloat(3, Float.parseFloat(txtCq.getText()));
			pst.setFloat(4, Float.parseFloat(txtCp.getText()));
			pst.setFloat(5, Float.parseFloat(txtBq.getText()));
			pst.setFloat(6, Float.parseFloat(txtBp.getText()));
			// Update dos only if Datepicker contains valid value
			if (isDateOfMilkSubscriptionStartValid())
				pst.setDate(7, java.sql.Date.valueOf(dtpDos.getValue()));

			pst.setString(8, imgPath);
			if (imgPath != "nil") {
				imgNoFace.setVisible(false);
				imgCustomer.setImage(new Image(imgPath));
			}
			int rowsAffected = pst.executeUpdate();
			if(rowsAffected == 1) {
				System.out.println("INFO: Customer updated: " + name);
				showAlert("Update Successful", "Customer updated successfully", Alert.AlertType.INFORMATION);
			} else {
				System.out.println("ERROR: Update failed for " + name);
				showAlert("Update Failed", "Update failed for customer " + name + ". Please reach out to team.", Alert.AlertType.ERROR);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void fillCombo() {
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
		comboCust.getItems().addAll(ary);
	}

	@FXML
	void initialize() {
		// TODO: use single imageView and render default or customer photo at runtime. Don't mantain 2 imageView.
		imgNoFace.setVisible(true);
		dtpDos.setValue(LocalDate.now());
		con = DBConnection.doConnect();
		fillCombo();
		txtCp.setText("0");
		txtCq.setText("0");
		txtBp.setText("0");
		txtBq.setText("0");
	}
}
