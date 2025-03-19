package com.example.womanstorefinal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;

import javax.xml.transform.Source;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button cancelBtn;
    @FXML
    private Label loginMsg;
    @FXML
    private ImageView brandLogo;
    @FXML
    private ImageView lockImg;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label loginMsg1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File brandingFile = new File("Images/login final.png");
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandLogo.setImage(brandingImage);

        File lockFile = new File("Images/lockkk.jpg");
        Image lockImage = new Image(lockFile.toURI().toString());
        lockImg.setImage(lockImage);
    }

    public void loginBtnOnAction(ActionEvent event) {
        if(usernameTextField.getText().isBlank()==false && passwordTextField.getText().isBlank()==false )
        {
            loginMsg.setText("Trying to log in..");

            validateLogin();
  
        }
        else
        {
            loginMsg.setText("Please enter your username and password.");
        }
    }

    public void cancelBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void createAccountForm(){

        try{

            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage regstage = new Stage();
            regstage.initStyle(StageStyle.UNDECORATED);
            regstage.setScene(new Scene(root,520,400));
            regstage.show();
            loginMsg.setText("");
            usernameTextField.setText("");
            passwordTextField.setText("");


        } catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public void validateLogin() {
        DatabaseConnection connect = new DatabaseConnection();
        Connection connectDB = connect.getConnection();
        String verifyLogin = "SELECT account_id, username FROM user_account WHERE username = '" + usernameTextField.getText() + "' AND password = '" + passwordTextField.getText() + "';";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                int accountId = queryResult.getInt("account_id");  // Get account_id
                loginMsg1.setText("Log in successful!");
                loginMsg.setText("");

                // Pass accountId to the shop form
                createShopForm(accountId);  // Pass the account_id to the shop form
            } else {
                loginMsg.setText("Invalid inputs.. Please try again.");
                loginMsg1.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            loginMsg.setText("Database connection failed.");
        }
    }

    public void createShopForm(int accountId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("shop.fxml"));
            Parent root = loader.load();

            // Pass account_id to ShopController
            ShopController shopController = loader.getController();
            shopController.setAccountId(accountId);  // Set the account_id in ShopController

            // Call the initialize method with the accountId to load products and other details
            shopController.initialize(accountId);

            Stage regstage = new Stage();
            regstage.initStyle(StageStyle.UNDECORATED);
            regstage.setScene(new Scene(root, 900, 750));
            regstage.show();

            loginMsg.setText("");
            usernameTextField.setText("");
            passwordTextField.setText("");
            loginMsg1.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}