package src.Client.main;

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
  private Button alertBtn, logInBtn;

  @FXML
  protected void onLogInButtonClick()throws IOException { //socket
    //Chek if account exists; server's work

    if( !accountText.getText().matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") ){
      Stage failureStage = new Stage();
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/main/failure.fxml"));
      failureStage.setScene(new Scene(fxmlLoader.load()));
      failureStage.show();
    }else{
      Stage mailStage = new Stage();
      FXMLLoader inboxLoader = new FXMLLoader(getClass().getResource("../resources/main/inbox.fxml"));
      mailStage.setTitle("Mail Box");
      mailStage.setScene(new Scene(inboxLoader.load()));
      InboxController mailController = inboxLoader.getController();
      mailController.setUserMail(accountText.getText());
      mailStage.show();

      Stage logInStage = (Stage) logInBtn.getScene().getWindow();
      logInStage.close();
    }


  }

  @FXML
  protected void onRegisterLabelClick() throws IOException {  //socket
    //Si dovrà passare i text al server per fare sign in;
    //System.out.println(accountText.getText() + passwordText.getText());

    Stage successStage = new Stage();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/main/success.fxml"));
    successStage.setScene(new Scene(fxmlLoader.load()));
    successStage.show();
  }

  @FXML
  protected void onAlertButtonClick() throws IOException {
    Stage alertStage = (Stage) alertBtn.getScene().getWindow();
    alertStage.close();
  }


}