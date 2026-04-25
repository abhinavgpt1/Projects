/**
 * Sample Skeleton for 'VariationDisplayView.fxml' Controller Class
 */

package com.example.milkman_legacy.variationDisplay;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.example.milkman_legacy.util.UIHelper.showAlert;

public class VariationDisplayController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="dtpDateFrom"
	private DatePicker dtpDateFrom; // Value injected by FXMLLoader

	@FXML // fx:id="comboName"
	private ComboBox<String> comboName; // Value injected by FXMLLoader

	@FXML // fx:id="dtpDateTo"
	private DatePicker dtpDateTo; // Value injected by FXMLLoader

	@FXML // fx:id="tbl"
	private TableView<VariationConsoleBean> tbl; // Value injected by FXMLLoader

	Connection con;

	private boolean isNameSelected() {
		String selectedName = comboName.getSelectionModel().getSelectedItem();
		if(selectedName == null || selectedName.isBlank()) {
			System.out.println("ERROR: Customer not selected from list: " + selectedName);
			showAlert("Select Customer", "Select a customer from list to see variation logs", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isFromDateValid() {
		if (dtpDateFrom.getValue() == null) {
			System.out.println("ERROR: Invalid From Date: " + dtpDateFrom.getEditor().getText());
			showAlert("Invalid From Date", "Enter a valid date in dd/mm/yyyy format", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isToDateValid() {
		if (dtpDateTo.getValue() == null) {
			System.out.println("ERROR: Invalid To Date: " + dtpDateTo.getEditor().getText());
			showAlert("Invalid To Date", "Enter a valid date in dd/mm/yyyy format", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isFromDateBeforeOrEqualsToDate() {
		if (dtpDateFrom.getValue().isAfter(dtpDateTo.getValue())) {
			String errorMsg = String.format("From Date (%s) is greater than To Date (%s)", dtpDateFrom.getValue(), dtpDateTo.getValue());
			System.out.println("ERROR: " + errorMsg);
			showAlert("Invalid Date Range", errorMsg, Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isValidDateRange() {
		return isFromDateValid() && isToDateValid() && isFromDateBeforeOrEqualsToDate();
	}

	@FXML
	void doFind(ActionEvent event) {
		if(!isNameSelected()) {
			return;
		}
		String name = comboName.getSelectionModel().getSelectedItem();
		String query = "select * from variationconsole where sname=?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, name);
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void doShow(ActionEvent event) {
		// This function works over date range for the name selected
		if(!isNameSelected() || !isValidDateRange()) {
			return;
		}

		String name = comboName.getSelectionModel().getSelectedItem();
		String query = "select * from variationconsole where sname=? and cdate>=? and cdate<=?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, name);
			pst.setDate(2, java.sql.Date.valueOf(dtpDateFrom.getValue()));
			pst.setDate(3, java.sql.Date.valueOf(dtpDateTo.getValue()));
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void doShowAll(ActionEvent event) {
		String query = "select * from variationconsole";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setTableItemsFromPreparedStatement(PreparedStatement pst) {
		ObservableList<VariationConsoleBean> list = FXCollections.observableArrayList();
		try (ResultSet table = pst.executeQuery()) {
			while (table.next()) {
				String name = table.getString("sname");
				String date = table.getString("cdate");
				float cq = table.getFloat("cq");
				float bq = table.getFloat("bq");
				list.add(new VariationConsoleBean(name, date, cq, bq));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
		if (list.isEmpty()) {
			System.out.println("INFO: No records found for variations");
			showAlert("No Records Found", "No variation records found in database", Alert.AlertType.INFORMATION);
		}
	}

	private void fillOnboardedCustomers() {
		try {
			PreparedStatement pst = con.prepareStatement("select distinct sname from variationconsole");
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
		fillOnboardedCustomers();

		// TableView setup
		// TODO: can make this table editable with update button for every row
		TableColumn<VariationConsoleBean, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<VariationConsoleBean, String> date = new TableColumn<>("Date");
		date.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<VariationConsoleBean, Float> cq = new TableColumn<>("Cow qty");
		cq.setCellValueFactory(new PropertyValueFactory<>("cqty"));

		TableColumn<VariationConsoleBean, Float> bq = new TableColumn<>("Buffalo qty");
		bq.setCellValueFactory(new PropertyValueFactory<>("bqty"));

		tbl.getColumns().addAll(name, date, cq, bq);
	}
}