/**
 * Sample Skeleton for 'LoginView.fxml' Controller Class
 */

package com.example.milkman_legacy.login;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.milkman_legacy.util.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import static com.example.milkman_legacy.Constants.DASHBOARD_VIEW;
import static com.example.milkman_legacy.Constants.APP_LOGIN_SOUND;

public class LoginController {

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="txtId"
	private TextField txtId; // Value injected by FXMLLoader

	@FXML // fx:id="txtPassword"
	private PasswordField txtPassword; // Value injected by FXMLLoader

	@FXML // fx:id="btnLogin"
	private Button btnLogin; // Value injected by FXMLLoader

	Connection con;
	URL url;
	AudioClip audio;

	@FXML
	void doLoginIn(ActionEvent event) {
		if (txtId.getText().equals(""))
			showMsg("Enter ID");
		else if (txtPassword.getText().equals(""))
			showMsg("Enter Password");
		else {
			try {
				PreparedStatement pst = con.prepareStatement("select * from idpwd where id = ?");
				pst.setString(1, txtId.getText());
				ResultSet table = pst.executeQuery();
				table.next();
				String id = table.getString("id");
				String pwd = table.getString("password");
				if (id.equals(txtId.getText()) && pwd.equals(txtPassword.getText())) {
					url = getClass().getResource(APP_LOGIN_SOUND);
					audio = new AudioClip(url.toString());
					audio.play();
					try {
						// 0.5sec wait to overlap between hearing sound while dashboard loads
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Parent root = FXMLLoader.load(getClass().getResource(DASHBOARD_VIEW));
					Scene scene = new Scene(root);
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.show();
					Scene scene1 = (Scene) btnLogin.getScene();
					scene1.getWindow().hide();
					// IF the user forgets the password...then he/she has navicat installed LOL!
				} else {
					showMsg("Either ID or Password is Wrong");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void showMsg(String msg) {
		Alert al = new Alert(AlertType.ERROR);
		al.setTitle("ERROR");
		al.setContentText(msg);
		al.show();
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
	}
}