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
import static com.example.milkman_legacy.util.UIHelper.showAlert;

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

	private boolean isLoginIdValid() {
		String loginId = txtId.getText();
		if(loginId == null || loginId.isBlank()) {
			System.out.println("ERROR: Login id is null or blank: " + loginId);
			showAlert("Invalid Login ID", "Enter a valid non-empty Login Id", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	private boolean isLoginPasswordValid() {
		String loginPwd = txtPassword.getText();
		if(loginPwd == null || loginPwd.isBlank()) {
			System.out.println("ERROR: Login password is null or blank: " + loginPwd);
			showAlert("Invalid Password", "Enter a valid non-empty password", Alert.AlertType.ERROR);
			return false;
		}
		return true;
	}

	@FXML
	void doLoginIn(ActionEvent event) {
		if (!isLoginIdValid() || !isLoginPasswordValid())
			return;
		// If the admin / milkman forgets the password, then there's DB access
		// TODO: add a signup and forgot password page
		String query = "select * from idpwd where id = ?";
		try (PreparedStatement pst = con.prepareStatement(query)) {
			String loginIdInput = txtId.getText();
			String loginPasswordInput = txtPassword.getText();
			pst.setString(1, loginIdInput);
			ResultSet table = pst.executeQuery();

			if (table.next()) {
				// UserId found, now validate password
				String loginPwdDb = table.getString("password");
				if (!loginPwdDb.equals(loginPasswordInput)) {
					System.out.println("ERROR: Invalid password entered during login: " + loginPasswordInput);
					showAlert("Invalid Password", "Wrong password. Please try another one.", AlertType.ERROR);
					return;
				}
			} else {
				System.out.println("ERROR: Invalid login id entered during login: " + loginIdInput);
				showAlert("Invalid Login ID", "No user found with this Login Id. Enter a valid one.", AlertType.ERROR);
				return;
			}

			AudioClip audio = new AudioClip(getClass().getResource(APP_LOGIN_SOUND).toString());
			audio.play();
			try {
				// 0.5sec wait to overlap between hearing sound while dashboard loads
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Hide login window and display dashboard
			Scene loginPageScene = btnLogin.getScene();
			loginPageScene.getWindow().hide();

			Scene scene = new Scene(FXMLLoader.load(getClass().getResource(DASHBOARD_VIEW)));
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {
		con = DBConnection.doConnect();
	}
}