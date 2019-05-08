package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/** Controls the login screen */
public class LoginController {
	
  @FXML private TextField user;
  @FXML private PasswordField password;
  @FXML private Button loginButton;
  @FXML private Label statusLabel;
  
  private LoginManager manager;
 // public void initialize() {}
  
  public void initManager(LoginManager loginManager) {
	  manager = loginManager;
	  //model = new LoginModel();
	  
	   loginButton.setOnAction((e) -> {
//	    	  String userName = authorize();
//	          if (userName!= null) 
//	            loginManager.authenticated(userName);
		  login();
		});
	 
  }
  
  public void login() {
		String username = this.user.getText();
		String password = this.password.getText();

		// Validations
		
		if (username == null || username.trim().equals("") && 
				   (password == null || password.trim().equals(""))) {
			statusLabel.setText("User name / password Cannot be empty or spaces");
					return;
				}
		
		if (username == null || username.trim().equals("")) {
			statusLabel.setText("Username cannot be empty or spaces");
			return;

		}
		if (password == null || password.trim().equals("")) {
			statusLabel.setText("Password cannot be empty or spaces");
			return;
		}
		
		// authentication check
		checkCredentials(username, password);
	}
  
  public void checkCredentials(String username, String password) {
		Boolean isValid = true;
				// model.getCredentials(username, password);
		if (!isValid) {
			statusLabel.setText("User does not exist!");
			return;
		}
		else {
			manager.authenticated(username);
		}

	}

}
