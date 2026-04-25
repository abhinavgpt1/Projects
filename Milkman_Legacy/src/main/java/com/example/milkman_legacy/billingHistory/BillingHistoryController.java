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
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import static com.example.milkman_legacy.util.UIHelper.getDesktopPath;
import static com.example.milkman_legacy.util.UIHelper.showAlert;

public class BillingHistoryController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="billStatusToggleGroup"
	private ToggleGroup billStatusToggleGroup; // Value injected by FXMLLoader

	@FXML // fx:id="comboName"
	private ComboBox<String> comboName; // Value injected by FXMLLoader

	@FXML // fx:id="tbl"
	private TableView<BillPanelBean> tbl; // Value injected by FXMLLoader

	Connection con;
	private FilteredList<BillPanelBean> billPanelBeanTableList = null;

	private boolean isNameSelected() {
		String selectedName = comboName.getSelectionModel().getSelectedItem();
		if(selectedName == null || selectedName.isBlank()) {
			System.out.println("ERROR: Name not selected: " + selectedName);
			showAlert("Select Name", "Select a customer from list to see billing history", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	@FXML
	void doExportCSV(ActionEvent event) {
		FileChooser fileChooserWindow = new FileChooser();
		fileChooserWindow.setTitle("Save As");
		fileChooserWindow.setInitialDirectory(new File(getDesktopPath()));
		fileChooserWindow.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (.csv)", "*.csv"));
		File excelDumpFile = fileChooserWindow.showSaveDialog(null);
		if (excelDumpFile == null) {
			System.out.println("INFO: Looks like user doesn't want to save the excel yet");
			return;
		}

		String filePath = excelDumpFile.getAbsolutePath();
		// PTR: This check isn't needed if ExtensionFilter contains only 1 extension i.e. csv
		if (!filePath.endsWith(".csv") && !filePath.endsWith(".CSV")) {
			filePath += ".csv";
			// qq - why not append .csv in existing file object?
			// ans - File objects are immutable; their abstract pathname cannot be changed after creation.
			excelDumpFile = new File(filePath);
		}

		final String NEWLINE = "\n";
		final String CSV_DELIMITER = ",";
		try (Writer writer = new BufferedWriter(new FileWriter(excelDumpFile))) {
			String heading = "Name,Bill Start Date,Bill End Date,Amount,Cow Milk Qty,Buffalo Milk Qty,Paid/Unpaid" + NEWLINE;
			writer.write(heading);
			for (BillPanelBean billingRecord: tbl.getItems()) {
				String record = "";
				record += billingRecord.getName() + CSV_DELIMITER;
				record += billingRecord.getBdos() + CSV_DELIMITER;
				record += billingRecord.getBdoe() + CSV_DELIMITER;
				record += billingRecord.getBamt() + CSV_DELIMITER;
				record += billingRecord.getBcqty() + CSV_DELIMITER;
				record += billingRecord.getBbqty() + CSV_DELIMITER;
				record += billingRecord.getBstatus() + NEWLINE;
				writer.write(record);
			}
			writer.flush();

			// Display location to user and successful operation
			String infoMessage = "File downloaded successfully at path: " + filePath;
			System.out.println("INFO: " + infoMessage);
			showAlert("File Downloaded", infoMessage, Alert.AlertType.INFORMATION);
		} catch (Exception ex) {
			System.out.println("ERROR: File couldn't be downloaded at path: " + filePath + " due to error: " + ex.getMessage());
			showAlert("Download Failed", "File couldn't be downloaded, please check with the team", Alert.AlertType.ERROR);
			ex.printStackTrace();
		}
	}

	@FXML
	void doPaid(ActionEvent event) {
		if (billPanelBeanTableList == null)
			return;
		billPanelBeanTableList.setPredicate(billPanelBean -> "paid".equals(billPanelBean.getBstatus()));
	}

	@FXML
	void doUnpaid(ActionEvent event) {
		if (billPanelBeanTableList == null)
			return;
		// Paid/Unpaid radio buttons should ideally act as a filter on the data already fetched
		// instead of fetching all paid/unpaid records from database irrespective of name selected
		// => [Logically Incorrect] String queryForPst = "select * from billpanel where status=?"; // status = false,true
		billPanelBeanTableList.setPredicate(billPanelBean -> "unpaid".equals(billPanelBean.getBstatus()));
	}

	@FXML
	void doShowHistory(ActionEvent event) {
		if(!isNameSelected()) {
			return;
		}
		String name = comboName.getSelectionModel().getSelectedItem();
		String query = "select * from billpanel where sname=?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			pst.setString(1, name);
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void doShowAll(ActionEvent event) {
		// Fetching all billing history records (paid, unpaid)
		String query = "select * from billpanel";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			setTableItemsFromPreparedStatement(pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setTableItemsFromPreparedStatement(PreparedStatement pst) {
		ObservableList<BillPanelBean> list = FXCollections.observableArrayList();
		try (ResultSet table = pst.executeQuery()) {
			while (table.next()) {
				String name = table.getString("sname");
				String bdos = table.getString("dos");
				String bdoe = table.getString("doe");
				float bamt = table.getFloat("amount");
				float bcqty = table.getFloat("cqty");
				float bbqty = table.getFloat("bqty");
				String bstatus = "0".equals(table.getString("status")) ? "unpaid" : "paid";
				list.add(new BillPanelBean(name, bdos, bdoe, bamt, bcqty, bbqty, bstatus));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		billPanelBeanTableList = new FilteredList<>(list); // saved for reference for performing paid/unpaid radio operations.
		tbl.setItems(billPanelBeanTableList);
		if (list.isEmpty()) {
			System.out.println("INFO: No billing history records found");
			showAlert("No Records Found", "No billing history records found in database", Alert.AlertType.INFORMATION);
		}
	}

	private void fillOnboardedCustomers() {
		try (PreparedStatement pst = con.prepareStatement("select distinct sname from billpanel");
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
		fillOnboardedCustomers();

		// Setup TableView
		TableColumn<BillPanelBean, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<BillPanelBean, String> bdos = new TableColumn<>("DOS");
		bdos.setCellValueFactory(new PropertyValueFactory<>("Bdos"));
		TableColumn<BillPanelBean, String> bdoe = new TableColumn<>("DOE");
		bdoe.setCellValueFactory(new PropertyValueFactory<>("Bdoe"));
		TableColumn<BillPanelBean, Float> cq = new TableColumn<>("Cow's");
		cq.setCellValueFactory(new PropertyValueFactory<>("Bcqty"));
		TableColumn<BillPanelBean, Float> bq = new TableColumn<>("Buff's");
		bq.setCellValueFactory(new PropertyValueFactory<>("Bbqty"));
		TableColumn<BillPanelBean, Float> amt = new TableColumn<>("Amt");
		amt.setCellValueFactory(new PropertyValueFactory<>("Bamt"));
		TableColumn<BillPanelBean, String> status = new TableColumn<>("Status");
		status.setCellValueFactory(new PropertyValueFactory<>("Bstatus"));

		tbl.getColumns().addAll(name, bdos, bdoe, cq, bq, amt, status);
	}
}