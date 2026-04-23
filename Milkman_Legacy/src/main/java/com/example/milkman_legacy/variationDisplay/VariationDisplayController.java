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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

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
	private TableView<CustomerBean> tbl; // Value injected by FXMLLoader

	Connection con;
	ResultSet table;

	@FXML
	void doFind(ActionEvent event) {
		String name = comboName.getSelectionModel().getSelectedItem();

		try {
			PreparedStatement pst = con.prepareStatement("select * from variationconsole where sname=?");
			pst.setString(1, name);

			getRecordsFromDatabase(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	@FXML
	void doShow(ActionEvent event) {
		// this function works for the name selected
		String name = comboName.getSelectionModel().getSelectedItem();
		try {
			// check if dtpdateFrom > dtpdateTo
			if (dtpDateTo.getValue().isBefore(dtpDateFrom.getValue()))
				showMsg("'Date To' cannot be before 'Date From'");

			else {
				PreparedStatement pst = con
						.prepareStatement("select * from variationconsole where sname=? and cdate>=? and cdate<=?");
				pst.setString(1, name);
				pst.setDate(2, java.sql.Date.valueOf(dtpDateFrom.getValue()));
				pst.setDate(3, java.sql.Date.valueOf(dtpDateTo.getValue()));
				getRecordsFromDatabase(pst);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	void showMsg(String msg) {
		Alert al = new Alert(AlertType.ERROR);
		al.setTitle("Errrrror");
		al.setContentText(msg);
		al.show();
	}

	@FXML
	void doShowAll(ActionEvent event) {
		try {
			PreparedStatement pst = con.prepareStatement("select * from variationconsole");
			getRecordsFromDatabase(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);

	}

	ObservableList<CustomerBean> list;

	void getRecordsFromDatabase(PreparedStatement pst) {
		// creating list of CustomerBean
		list = FXCollections.observableArrayList();
		try {
			table = pst.executeQuery();

			while (table.next()) {
				String name = table.getString("sname");
				String date = table.getString("cdate");
				float cq = table.getFloat("cq");
				float bq = table.getFloat("bq");
				CustomerBean obj = new CustomerBean(name, date, cq, bq);
				list.add(obj);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	void fillCombo() {
		try {
			PreparedStatement pst = con.prepareStatement("select distinct sname from variationconsole");
			table = pst.executeQuery();
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
		// setting up columns of TableView
		TableColumn<CustomerBean, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<CustomerBean, String> date = new TableColumn<>("Date");
		date.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<CustomerBean, Float> cq = new TableColumn<>("Cow qty");
		cq.setCellValueFactory(new PropertyValueFactory<>("cqty"));

		TableColumn<CustomerBean, Float> bq = new TableColumn<>("Buffalo qty");
		bq.setCellValueFactory(new PropertyValueFactory<>("bqty"));

		// clearing existing columns(if any)[the ones in scene builder(c1,c2)]
		tbl.getColumns().clear();
		// adding own columns in the table
		tbl.getColumns().addAll(name, date, cq, bq);
	}
}