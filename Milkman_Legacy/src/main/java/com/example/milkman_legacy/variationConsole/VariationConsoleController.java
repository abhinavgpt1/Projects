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

import com.example.milkman_legacy.dbutil.DBConnection;
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

	@FXML
	void doReset(ActionEvent event) {
		imgCustomer.setVisible(false);
		imgNoFace.setVisible(false);
		listCust.getItems().clear();
		fillList();
		lblBq.setText("");
		lblCq.setText("");
		txtBq.setText("");
		txtCq.setText("");
		chkNil.setSelected(false);
		dtpDate.getEditor().setText("");
	}

	Connection con;

	@FXML
	void doDelete(ActionEvent event) {
		ObservableList<String> lstFull = listCust.getItems();
		ObservableList<String> lstSelected = listCust.getSelectionModel().getSelectedItems();
		lstFull.retainAll(lstSelected);
	}

	@FXML
	void doSave(ActionEvent event) {
		String name = listCust.getSelectionModel().getSelectedItem();
		// check if a variation corresponding name exists

		if (name != null) {
			try {
				PreparedStatement pst = con.prepareStatement("insert into variationconsole values(?,?,?,?)");
				pst.setString(1, name);

				LocalDate lwdate = dtpDate.getValue();
				if (lwdate == null) {
					String stwdate = dtpDate.getEditor().getText();
					lwdate = LocalDate.parse(stwdate);
				}
				pst.setDate(2, java.sql.Date.valueOf(lwdate));

				if (Float.parseFloat(lblCq.getText()) == 0)
					txtCq.setText("0");
				if (Float.parseFloat(lblBq.getText()) == 0)
					txtBq.setText("0");

				if (chkNil.isSelected()) {
					pst.setFloat(3, -Float.parseFloat(lblCq.getText()));
					pst.setFloat(4, -Float.parseFloat(lblBq.getText()));
				} else {
					// checking if abs(variation in milk) doesn't go below base milk qty
					if (Float.parseFloat(txtCq.getText()) < 0)
						if (Float.parseFloat(lblCq.getText()) <= Math.abs(Float.parseFloat(txtCq.getText())))
							showMsg("Check cow qty. Input value exceeds base value");
					if (Float.parseFloat(txtBq.getText()) < 0)
						if (Float.parseFloat(lblBq.getText()) <= Math.abs(Float.parseFloat(txtBq.getText())))
							showMsg("Check buffalo qty. Input value exceeds base value");

					pst.setFloat(3, Float.parseFloat(txtCq.getText()));
					pst.setFloat(4, Float.parseFloat(txtBq.getText()));
				}
				pst.executeUpdate();
				listCust.getItems().remove(name);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else
			showMsg("Select Name");
	}

	void showMsg(String msg) {
		Alert al = new Alert(AlertType.ERROR);
		al.setTitle("ERROR");
		al.setContentText(msg);
		al.show();
	}

	@FXML
	void doDoubleClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			imgCustomer.setVisible(false);
			imgNoFace.setVisible(false);
			String name = listCust.getSelectionModel().getSelectedItem();
			// System.out.println("Worked");
			try {
				PreparedStatement pst = con.prepareStatement("select cq,bq,imgpath from customerentry where sname=?");
				pst.setString(1, name);
				ResultSet table = pst.executeQuery();
				table.next();
				lblCq.setText(String.valueOf(table.getFloat("cq")));
				lblBq.setText(String.valueOf(table.getFloat("bq")));
				// txtCq.setText(String.valueOf(-table.getFloat("cq"))); OPTIONAL TO KEEP[ (as
				// stated below)]
				// txtBq.setText(String.valueOf(-table.getFloat("bq"))); OPTIONAL TO KEEP[a
				// replacement to chkNil]
				String path = table.getString("imgpath");
				if (path.equals("nil"))
					imgNoFace.setVisible(true);
				else {
					imgCustomer.setImage(new Image(path));
					imgCustomer.setVisible(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void fillList() {
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
		con = DBConnection.doConnect();
		fillList();
		listCust.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		imgNoFace.setVisible(false);
		lblCq.setText("0");
		lblBq.setText("0");
	}
}