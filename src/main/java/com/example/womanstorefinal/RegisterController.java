package com.example.womanstorefinal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

//import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ResourceBundle;




public class RegisterController implements Initializable {


    @FXML
    private ImageView shieldImageView;
    @FXML
    private ImageView registerImageView;
    @FXML
    private Button closeButton;
    @FXML
    private Label registrationMsgLabel;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField UsernameTextField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File registerFile = new File("Images/Register.png");
        Image registerImage = new Image(registerFile.toURI().toString());
        registerImageView.setImage(registerImage);

        File shieldFile = new File("Images/shld.png");
        Image shieldImage = new Image(shieldFile.toURI().toString());
        shieldImageView.setImage(shieldImage);
    }

    public void closeButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        //Platform.exit();
    }

    public void registerButtonOnAction(ActionEvent actionEvent) {
        if(setPasswordField.getText()==""|| confirmPasswordField.getText()==""||firstnameTextField.getText()==""||lastnameTextField.getText()==""||UsernameTextField.getText()==""){
            confirmPasswordLabel.setText("Please fill all the required fields.");

        }
        else {
            confirmPasswordLabel.setText("");
            if(setPasswordField.getText().equals(confirmPasswordField.getText()))
            {
                confirmPasswordLabel.setText("");
                registrationMsgLabel.setText("");
                registerUser();

            }
            else
            {
                registrationMsgLabel.setText("");
                confirmPasswordLabel.setText("Passwords do not match..");
            }

        }


    }

    public void registerUser(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String username = UsernameTextField.getText();
        String password = setPasswordField.getText();

        // Setting default values for capital, income, and cost
        double initialCapital = 10000.0;
        double initialIncome = 0.0;
        double initialCost = 0.0;

        String insertFields = "INSERT INTO user_account(lastname, firstname, username, password, capital, income, cost) VALUES('";
        String insertValues = lastname + "','" + firstname + "','" + username + "','" + password + "','" + initialCapital + "','" + initialIncome + "','" + initialCost + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement stmt = connectDB.createStatement();
            stmt.executeUpdate(insertToRegister);
            registrationMsgLabel.setText("User registered successfully!");

            // Clear input fields after successful registration
            setPasswordField.setText("");
            confirmPasswordField.setText("");
            UsernameTextField.setText("");
            firstnameTextField.setText("");
            lastnameTextField.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }




}
