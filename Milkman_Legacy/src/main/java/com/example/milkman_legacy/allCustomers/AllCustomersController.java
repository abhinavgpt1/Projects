/**
 * Sample Skeleton for 'AllCustomersView.fxml' Controller Class
 */

package com.example.milkman_legacy.allCustomers;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class AllCustomersController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="comboDate"
	private ComboBox<String> comboDate; // Value injected by FXMLLoader

	@FXML // fx:id="radCow"
	private RadioButton radCow; // Value injected by FXMLLoader

	@FXML // fx:id="select"
	private ToggleGroup select; // Value injected by FXMLLoader

	@FXML // fx:id="radBuff"
	private RadioButton radBuff; // Value injected by FXMLLoader

	@FXML // fx:id="tbl"
	private TableView<CustomerBean> tbl; // Value injected by FXMLLoader

	Connection con;
	ResultSet table;

	@FXML
	void doFetchBuyers(ActionEvent event) {

		if (radCow.isSelected()) {
			try {
				// fetching records where cow qty !=0
				PreparedStatement pst = con.prepareStatement("select * from customerentry where cq<> ?");
				pst.setFloat(1, 0);
				getRecordFromDatabase(pst);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (radBuff.isSelected()) {
			try {
				// fetching records where buff. qty !=0
				PreparedStatement pst = con.prepareStatement("select * from customerentry where bq<> ?");
				pst.setFloat(1, 0);
				getRecordFromDatabase(pst);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		tbl.setItems(list);
	}

	@FXML
	void doFetchByDate(ActionEvent event) {
		String dos = comboDate.getSelectionModel().getSelectedItem();
		try {
			PreparedStatement pst = con.prepareStatement("select * from customerentry where dos=?");
			pst.setString(1, dos);

			getRecordFromDatabase(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	@FXML
	void doShow(ActionEvent event) {
		try {
			// by specifying column names a fixed data is fetched(which is usable)[we've
			// avoided fetching the whole record]
			PreparedStatement pst = con.prepareStatement("select sname,cq,cprice,bq,bprice,dos from customerentry");
			getRecordFromDatabase(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	ObservableList<CustomerBean> list;

	void getRecordFromDatabase(PreparedStatement pst) {
		// creating observable list
		list = FXCollections.observableArrayList();
		try {
			table = pst.executeQuery();
			while (table.next()) {
				String name = table.getString("sname");
				float cq = table.getFloat("cq");
				float cp = table.getFloat("cprice");
				float bq = table.getFloat("bq");
				float bp = table.getFloat("bprice");
				String dos = table.getString("dos");
				CustomerBean obj = new CustomerBean(name, cp, cq, bp, bq, dos);
				list.add(obj);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void fillCombo() {
		try {
			PreparedStatement pst = con.prepareStatement("select distinct dos from customerentry");
			table = pst.executeQuery();
			while (table.next()) {
				comboDate.getItems().add(table.getString("dos"));// table.getDate("dos").toString() is also fine
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
		fillCombo();
		// creating our own columns
		TableColumn<CustomerBean, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<CustomerBean, Float> cq = new TableColumn<>("Cow qty");
		cq.setCellValueFactory(new PropertyValueFactory<>("cqty"));

		TableColumn<CustomerBean, Float> cp = new TableColumn<>("Cow price");
		cp.setCellValueFactory(new PropertyValueFactory<>("cprice"));

		TableColumn<CustomerBean, Float> bq = new TableColumn<>("Buff qty");
		bq.setCellValueFactory(new PropertyValueFactory<>("bqty"));

		TableColumn<CustomerBean, Float> bp = new TableColumn<>("Buff price");
		bp.setCellValueFactory(new PropertyValueFactory<>("bprice"));

		TableColumn<CustomerBean, String> dos = new TableColumn<>("D.O.S");
		dos.setCellValueFactory(new PropertyValueFactory<>("dos"));

		// clearing existing columns(if any)
		tbl.getColumns().clear();
		// adding our own columns
		tbl.getColumns().addAll(name, cq, cp, bq, bp, dos);

	}
}