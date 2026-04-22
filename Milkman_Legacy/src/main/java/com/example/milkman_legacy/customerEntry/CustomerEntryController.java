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

	@FXML
	void doBrowse(ActionEvent event) {
		// Selecting an image
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Select Profile Photo");
		filechooser.setInitialDirectory(new File("C:\\Users\\Abhinav Gupta\\Desktop\\java\\FxProjects\\Milkman\\src"));
		// while writing path in program, use\\
		filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
		// folders will be displayed along with images of formats mentioned

		File selectedfile = filechooser.showOpenDialog(null);
		if (selectedfile != null) {
			imgCustomer.setImage(new javafx.scene.image.Image(selectedfile.toURI().toString()));
			imgPath = selectedfile.toURI().toString();
		}
	}

	@FXML
	void doDelete(ActionEvent event) {
		imgNoFace.setVisible(false);
		String name = comboCust.getSelectionModel().getSelectedItem();
		try {
			PreparedStatement pst = con.prepareStatement("delete from customerentry where sname=?");
			pst.setString(1, name);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		doNew(event);
	}

	@FXML
	void doFetch(ActionEvent event) {
		imgCustomer.setVisible(false);
		imgNoFace.setVisible(false);
		String name = comboCust.getSelectionModel().getSelectedItem();
		if (name != null) {
			try {
				PreparedStatement pst = con.prepareStatement("select * from customerentry where sname=?");
				pst.setString(1, name);
				ResultSet table = pst.executeQuery();
				table.next();
				txtMob.setText(table.getString("mobile"));
				txtAddress.setText(table.getString("address"));
				txtCq.setText(String.valueOf(table.getFloat("cq")));
				txtCp.setText(String.valueOf(table.getFloat("cprice")));
				txtBq.setText(String.valueOf(table.getFloat("bq")));
				txtBp.setText(String.valueOf(table.getFloat("bprice")));
				java.sql.Date swdos = table.getDate("dos");

				dtpDos.getEditor().setText(swdos.toString());
				String path = table.getString("imgpath");
				imgPath = path;
				if (path.equals("nil"))
					imgNoFace.setVisible(true);
				else {
					imgCustomer.setImage(new Image(path));
					imgCustomer.setVisible(true);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@FXML
	void doNew(ActionEvent event) {
		imgNoFace.setVisible(false);
		txtAddress.setText("");
		txtMob.setText("");
		txtBp.setText("");
		txtBq.setText("");
		txtCp.setText("");
		txtCq.setText("");
		comboCust.getEditor().setText("");
		dtpDos.getEditor().setText("");
		imgCustomer.setImage(new Image("\\"));
	}

	@FXML
	void doSave(ActionEvent event) {

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
			if (dtpDos.getValue() == null)
				pst.setDate(8, java.sql.Date.valueOf(LocalDate.parse(dtpDos.getEditor().getText())));
			else
				pst.setDate(8, java.sql.Date.valueOf(dtpDos.getValue()));
			pst.setString(9, imgPath);

			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	void doUpdate(ActionEvent event) {

		String name = comboCust.getSelectionModel().getSelectedItem();
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
			if (dtpDos.getValue() != null)
				pst.setDate(7, java.sql.Date.valueOf(dtpDos.getValue()));
			else {
				String stdos = dtpDos.getEditor().getText();
				LocalDate lwdos = LocalDate.parse(stdos);
				pst.setDate(7, java.sql.Date.valueOf(lwdos));
			}
			pst.setString(8, imgPath);
			if (imgPath != "nil") {
				imgNoFace.setVisible(false);
				imgCustomer.setImage(new Image(imgPath));
			}
			pst.executeUpdate();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		comboCust.getItems().addAll(ary);
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		imgNoFace.setVisible(false);
		con = DBConnection.doConnect();
		fillCombo();
		txtCp.setText("0");
		txtCq.setText("0");
		txtBp.setText("0");
		txtBq.setText("0");
	}
}
