/**
 * Sample Skeleton for 'AllCustomersView.fxml' Controller Class
 */

package com.example.milkman_legacy.allCustomers;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.example.milkman_legacy.util.UIHelper.showAlert;

public class AllCustomersController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="comboDate"
	private ComboBox<String> comboDate; // Value injected by FXMLLoader

	@FXML // fx:id="radCow"
	private RadioButton radCow; // Value injected by FXMLLoader

	@FXML // fx:id="milkTypeToggleGroup"
	private ToggleGroup milkTypeToggleGroup; // Value injected by FXMLLoader

	@FXML // fx:id="radBuff"
	private RadioButton radBuff; // Value injected by FXMLLoader

	@FXML // fx:id="tbl"
	private TableView<CustomerEntryBean> tbl; // Value injected by FXMLLoader

	Connection con;
	private FilteredList<CustomerEntryBean> customerEntryBeanTableList = null;

	private boolean isDateOfSubscriptionStartValid() {
		String selectedDate = comboDate.getSelectionModel().getSelectedItem();
		try {
			LocalDate.parse(selectedDate); // by default DateFormatter is ISO_LOCAL_DATE
			return true;
		} catch (DateTimeParseException | NullPointerException e) {
			System.out.println("ERROR: Date not selected: " + selectedDate);
			showAlert("Invalid Date", "Select a date from list or enter a valid one in format YYYY-MM-DD", Alert.AlertType.ERROR);
			return false;
		}
	}

	@FXML
	void doFilterByMilkType(ActionEvent event) {
		if (customerEntryBeanTableList == null || milkTypeToggleGroup.getSelectedToggle() == null)
			return;
		// Cow/Buffalo radio buttons should ideally act as a filter on the data already fetched
		// instead of fetching all cow/buffalo milk consuming customers from database irrespective of date selected.
		// => [Logically Incorrect] String queryForPst = "select * from customerentry where cq <> 0"; // for cow milk consuming customers
		if (radCow.isSelected()) {
			customerEntryBeanTableList.setPredicate(customerEntry -> customerEntry.getCqty() != 0);
		} else {
			customerEntryBeanTableList.setPredicate(customerEntry -> customerEntry.getBqty() != 0);
		}
	}

	@FXML
	void doFetchByDate(ActionEvent event) {
		if (!isDateOfSubscriptionStartValid()) {
			return;
		}
		String dateOfSubscriptionStart = comboDate.getSelectionModel().getSelectedItem();
		String query = "select * from customerentry where dos=?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			pst.setDate(1, Date.valueOf(dateOfSubscriptionStart));
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void doShowAllCustomers(ActionEvent event) {
		String query = "select sname, cq, cprice, bq, bprice, dos from customerentry";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setTableItemsFromPreparedStatement(PreparedStatement pst) {
		ObservableList<CustomerEntryBean> list = FXCollections.observableArrayList();
		try (ResultSet table = pst.executeQuery()) {
			while (table.next()) {
				String name = table.getString("sname");
				float cq = table.getFloat("cq");
				float cp = table.getFloat("cprice");
				float bq = table.getFloat("bq");
				float bp = table.getFloat("bprice");
				String dos = table.getString("dos");
				CustomerEntryBean obj = new CustomerEntryBean(name, cp, cq, bp, bq, dos);
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		customerEntryBeanTableList = new FilteredList<>(list); // saved for reference for performing paid/unpaid radio operations.
		tbl.setItems(customerEntryBeanTableList);
		if (list.isEmpty()) {
			System.out.println("INFO: No customer records found");
			showAlert("No Records Found", "No customer records found in database", Alert.AlertType.INFORMATION);
		}
	}

	private void fillDistinctSubscriptionStartDates() {
		try (PreparedStatement pst = con.prepareStatement("select distinct dos from customerentry");
			 ResultSet table = pst.executeQuery()) {
			while (table.next()) {
				comboDate.getItems().add(table.getDate("dos").toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
		fillDistinctSubscriptionStartDates();

		// Setup TableView
		TableColumn<CustomerEntryBean, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<CustomerEntryBean, Float> cq = new TableColumn<>("Cow Milk (/day)");
		cq.setCellValueFactory(new PropertyValueFactory<>("cqty"));
		TableColumn<CustomerEntryBean, Float> cp = new TableColumn<>("Cow Milk Price");
		cp.setCellValueFactory(new PropertyValueFactory<>("cprice"));
		TableColumn<CustomerEntryBean, Float> bq = new TableColumn<>("Buffalo Milk (/day)");
		bq.setCellValueFactory(new PropertyValueFactory<>("bqty"));
		TableColumn<CustomerEntryBean, Float> bp = new TableColumn<>("Buffalo Milk Price");
		bp.setCellValueFactory(new PropertyValueFactory<>("bprice"));
		TableColumn<CustomerEntryBean, String> dos = new TableColumn<>("DOS");
		dos.setCellValueFactory(new PropertyValueFactory<>("dos"));

		tbl.getColumns().addAll(name, cq, cp, bq, bp, dos);

	}
}