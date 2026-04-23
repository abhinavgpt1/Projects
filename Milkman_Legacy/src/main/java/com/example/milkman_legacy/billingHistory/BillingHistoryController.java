/**
 * Sample Skeleton for 'BillingHistoryView.fxml' Controller Class
 */

package com.example.milkman_legacy.billingHistory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class BillingHistoryController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="alternate"
	private ToggleGroup alternate; // Value injected by FXMLLoader

	@FXML // fx:id="comboName"
	private ComboBox<String> comboName; // Value injected by FXMLLoader

	@FXML // fx:id="tbl"
	private TableView<CustomerBean> tbl; // Value injected by FXMLLoader

	Connection con;
	ResultSet table;

	@FXML
	void doExportCSV(ActionEvent event) {
		try {
			writeExcel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeExcel() throws Exception {
		Writer writer = null;
		try {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Select Path:");
			chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
			// save dialog box gets opened
			File file = chooser.showSaveDialog(null);
			String filePath = file.getAbsolutePath();
			// check if there doesn't exist extension name
			if (!(filePath.endsWith(".csv") || filePath.endsWith(".CSV"))) {
				filePath = filePath + ".csv";
			}
			file = new File(filePath);

			writer = new BufferedWriter(new FileWriter(file));
			String text = "Name,date of start,date of end, Amount,Cow's Milk,Buffalo's Milk,Status\n";
			writer.write(text);
			for (CustomerBean p : list) {
				text = p.getName() + "," + p.getBdos() + "," + p.getBdoe() + "," + p.getBamt() + p.getBcqty() + ","
						+ p.getBbqty() + "," + p.getBstatus() + "\n";
				writer.write(text);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			writer.flush();
			writer.close();
		}
	}

	@FXML
	void doPaid(ActionEvent event) {

		try {
			// selecting all customers with paid bills
			PreparedStatement pst = con.prepareStatement("select * from billpanel where status=?");
			pst.setBoolean(1, true);
			getRecordsFromDatabase(pst);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	@FXML
	void doUnpaid(ActionEvent event) {

		try {
			// selecting all customers with paid bills
			PreparedStatement pst = con.prepareStatement("select * from billpanel where status=?");
			pst.setBoolean(1, false);
			getRecordsFromDatabase(pst);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);

	}

	@FXML
	void doShow(ActionEvent event) {

		String name = comboName.getSelectionModel().getSelectedItem();
		try {
			// fetching bills for respective name
			PreparedStatement pst = con.prepareStatement("select * from billpanel where sname=?");
			pst.setString(1, name);
			getRecordsFromDatabase(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	@FXML
	void doShowAll(ActionEvent event) {

		try {
			// fetching whole data of bills(pending+paid)
			PreparedStatement pst = con.prepareStatement("select * from billpanel");
			getRecordsFromDatabase(pst);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		tbl.setItems(list);
	}

	ObservableList<CustomerBean> list;

	void getRecordsFromDatabase(PreparedStatement pst) {
		list = FXCollections.observableArrayList();
		try {
			table = pst.executeQuery();

			while (table.next()) {
				String name = table.getString("sname");
				String Bdos = table.getString("dos");
				String Bdoe = table.getString("doe");
				float Bamt = table.getFloat("amount");
				float Bcqty = table.getFloat("cqty");
				float Bbqty = table.getFloat("bqty");
				String Bstatus = table.getString("status");

				if (Bstatus.equals("0"))
					Bstatus = "unpaid";
				else
					Bstatus = "paid";
				CustomerBean obj = new CustomerBean(name, Bdos, Bdoe, Bamt, Bcqty, Bbqty, Bstatus);
				list.add(obj);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void fillCombo() {
		try {
			PreparedStatement pst = con.prepareStatement("select distinct sname from billpanel");
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
		// creating table columns
		TableColumn<CustomerBean, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<CustomerBean, String> bdos = new TableColumn<>("DOS");
		bdos.setCellValueFactory(new PropertyValueFactory<>("Bdos"));
		TableColumn<CustomerBean, String> bdoe = new TableColumn<>("DOE");
		bdoe.setCellValueFactory(new PropertyValueFactory<>("Bdoe"));
		TableColumn<CustomerBean, String> status = new TableColumn<>("Status");
		status.setCellValueFactory(new PropertyValueFactory<>("Bstatus"));

		TableColumn<CustomerBean, Float> cq = new TableColumn<>("Cow's");
		cq.setCellValueFactory(new PropertyValueFactory<>("Bcqty"));
		TableColumn<CustomerBean, Float> bq = new TableColumn<>("Buff's");
		bq.setCellValueFactory(new PropertyValueFactory<>("Bbqty"));
		TableColumn<CustomerBean, Float> amt = new TableColumn<>("Amt");
		amt.setCellValueFactory(new PropertyValueFactory<>("Bamt"));

		// clearing table if there exists columns already
		tbl.getColumns().clear();
		// adding our own columns
		tbl.getColumns().addAll(name, bdos, bdoe, cq, bq, amt, status);
	}
}