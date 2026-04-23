/**
 * Sample Skeleton for 'BillPanelView.fxml' Controller Class
 */

package com.example.milkman_legacy.billPanel;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.example.milkman_legacy.dbutil.DBConnection;
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

	@FXML
	void doGetVariation(ActionEvent event) {
		String name = listCust.getSelectionModel().getSelectedItem();

		try {
			// if dtpdoe<dtpdos
			if (dtpDoe.getValue().isBefore(dtpDos.getValue()))
				showMsg("Date of End cannot be before Date of Start");
			// if dtpdos<dos
			else {
				PreparedStatement pst = con.prepareStatement("select dos from customerentry where sname=?");
				pst.setString(1, name);
				ResultSet table = pst.executeQuery();
				table.next();
				LocalDate dos = LocalDate.parse(table.getDate("dos").toString());

				if (dtpDos.getValue().isBefore(dos))
					showMsg("'Date of Start' is before customer's dos");
				else {
					pst = con.prepareStatement("select sname from billpanel");
					table = pst.executeQuery();
					boolean test = false;
					// check if name exists in bill panel
					while (table.next()) {
						if (table.getString("sname").equals(name)) {
							test = true;
							break;
						}

					}
					if (test == true) {
						// getting the status of person(to check if there is "a" pending bill)
						pst = con.prepareStatement("select min(status) from billpanel where sname = ?");
						pst.setString(1, name);
						table = pst.executeQuery();
						table.next();

					}
					// test==false means first time variation of person
					if (test == false || (test && table.getBoolean("min(status)"))) {// for first time variation or if
																						// person has cleared all bills

						if ((test && table.getBoolean("min(status)"))) {// if person exits and has cleared all bills
							pst = con.prepareStatement("select max(doe) from billpanel where sname=? ");
							pst.setString(1, name);
							table = pst.executeQuery();
							table.next();
							// checking if dtpdos isn't before latest doe
							if (dtpDos.getValue().isBefore(LocalDate.parse(table.getDate("max(doe)").toString()))
									|| dtpDos.getValue().isEqual(LocalDate.parse(table.getDate("max(doe)").toString())))
								showMsg("DOS entered is less than or equal to the the latest PAID bill date");
						}

						pst = con.prepareStatement(
								"select sum(cq),sum(bq) from variationconsole where sname=?  and cdate>=? and cdate<=?");
						pst.setString(1, name);

						pst.setDate(2, java.sql.Date.valueOf(dtpDos.getValue()));
						pst.setDate(3, java.sql.Date.valueOf(dtpDoe.getValue()));
						table = pst.executeQuery();
						table.next();

						lblVCq.setText(String.valueOf(table.getFloat("sum(cq)")));
						lblVBq.setText(String.valueOf(table.getFloat("sum(bq)")));
					} else
						showMsg("Your Bill is Pending");
				}
			}

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void showMsg(String msg) {
		Alert al = new Alert(AlertType.ERROR);
		al.setTitle("Error");
		al.setContentText(msg);
		al.show();
	}

	@FXML
	void doDoubleClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			// getting info
			String name = listCust.getSelectionModel().getSelectedItem();
			try {
				imgNoFace.setVisible(false);
				imgCust.setVisible(false);
				PreparedStatement pst = con
						.prepareStatement("select cq,cprice,bq,bprice,imgpath from customerentry where sname=? ");
				pst.setString(1, name);
				// pst.executeUpdate(); IS WRONG HERE
				ResultSet table = pst.executeQuery();
				table.next();
				lblCq.setText(String.valueOf(table.getFloat("cq")));
				lblCp.setText(String.valueOf(table.getFloat("cprice")));
				lblBq.setText(String.valueOf(table.getFloat("bq")));
				lblBp.setText(String.valueOf(table.getFloat("bprice")));
				String path = table.getString("imgpath");
				if (path.equals("nil")) {
					imgNoFace.setVisible(true);
				} else {
					imgCust.setImage(new Image(path));
					imgCust.setVisible(true);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	@FXML
	void doBill(ActionEvent event) {
		// calculating bill
		LocalDate dos1 = dtpDos.getValue();
		LocalDate doe1 = dtpDoe.getValue();
		long days = ChronoUnit.DAYS.between(dos1, doe1);
		lblDays.setText(String.valueOf(days));
		float bill = 0;
		bill = (Float.parseFloat(lblCq.getText()) * Float.parseFloat(lblDays.getText())
				+ Float.parseFloat(lblVCq.getText())) * Float.parseFloat(lblCp.getText())
				+ (Float.parseFloat(lblBq.getText()) * Float.parseFloat(lblDays.getText())
						+ Float.parseFloat(lblVBq.getText())) * Float.parseFloat(lblBp.getText());
		lblBill.setText(String.valueOf((bill)));
	}

	@FXML
	void doSaveSms(ActionEvent event) {
		String name = listCust.getSelectionModel().getSelectedItem();
		try {

			PreparedStatement pst = con.prepareStatement("insert into billpanel values(null,?,?,?,?,?,?,?)");
			pst.setString(1, name);
			pst.setDate(2, java.sql.Date.valueOf(dtpDos.getValue()));
			pst.setDate(3, java.sql.Date.valueOf(dtpDoe.getValue()));
			pst.setFloat(4, Float.parseFloat(lblBill.getText()));
			float cqty = Float.parseFloat(lblCq.getText()) + Float.parseFloat(lblVCq.getText());
			pst.setFloat(5, cqty);
			float bqty = Float.parseFloat(lblBq.getText()) + Float.parseFloat(lblVBq.getText());
			pst.setFloat(6, bqty);
			pst.setBoolean(7, false);
			pst.executeUpdate();
			listCust.getItems().remove(name);

			// ***************SMS***********
			pst = con.prepareStatement("select mobile from customerentry where sname=?");
			pst.setString(1, name);
			ResultSet table = pst.executeQuery();
			table.next();
			String mob = table.getString("mobile");
			String s = "YOUR BILL FOR DATE " + dtpDos.getEditor().getText() + " TO " + dtpDoe.getEditor().getText()
					+ " IS PENDING.\nKINDLY, PAY IT ON TIME.";
			System.out.println(s);
			String resp = SST_SMS.bceSunSoftSend(mob, s);
			if (resp.contains("Exception"))
				showMsg("Check Internet Connection");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void fillList() {
		ArrayList<String> asl = new ArrayList<>();
		try {
			PreparedStatement pst = con.prepareStatement("select sname from customerentry");
			ResultSet table = pst.executeQuery();
			while (table.next()) {
				String name = table.getString("sname");
				asl.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		listCust.getItems().addAll(asl);
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
		imgNoFace.setVisible(false);
		lblBp.setText("0");
		lblCp.setText("0");
		lblCq.setText("0");
		lblBq.setText("0");
		lblVBq.setText("0");
		lblVCq.setText("0");
		fillList();
	}
}