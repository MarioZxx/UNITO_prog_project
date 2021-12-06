package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

  @FXML
  private TextField accountText;
  @FXML
  private PasswordField passwordText;
  @FXML
  private Button successButton, logInButton;

  @FXML
  protected void onLogInButtonClick()throws IOException {
    //Chek if account exists; server's work

    Stage mailStage = new Stage();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/main/inbox.fxml"));
    mailStage.setTitle("Mail Box");
    mailStage.setScene(new Scene(fxmlLoader.load()));
    mailStage.show();

    Stage logInStage = (Stage)logInButton.getScene().getWindow();
    logInStage.close();
  }

  @FXML
  protected void onRegisterLabelClick() throws IOException {
    //Si dovrà passare i text al server per fare sign in;
    //System.out.println(accountText.getText() + passwordText.getText());

    Stage successStage = new Stage();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/main/success.fxml"));
    successStage.setScene(new Scene(fxmlLoader.load()));
    successStage.show();
  }

  @FXML
  protected void onSuccessButtonClick() throws IOException {
    Stage logInStage = (Stage)successButton.getScene().getWindow();
    logInStage.close();
  }

}