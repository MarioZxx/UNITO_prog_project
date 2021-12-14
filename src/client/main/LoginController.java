package src.client.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import src.model.*;

public class LoginController {

  @FXML
  private Label alertLbl;
  @FXML
  private TextField accountText;
  @FXML
  private PasswordField passwordText;
  @FXML
  private Button alertBtn, logInBtn;
  private Socket socket;
  private ObjectInputStream inStream;
  private ObjectOutputStream outStream;

  @FXML
  protected void onLogInButtonClick() throws IOException {
    if( !accountText.getText().matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") ){
      loadAlertStage("Email syntax wrong!");
    }else{

        if(socket == null || socket.isClosed()) {
          socket = new Socket("127.0.0.1", 8189);
          inStream = new ObjectInputStream(socket.getInputStream());
          outStream = new ObjectOutputStream(socket.getOutputStream());
          outStream.flush();
        }
        try {
          outStream.writeObject(new Log(new Date(), accountText.getText(), "Try to login", "login", null));
          outStream.flush();

          if(inStream.readBoolean()) {
            Stage mailStage = new Stage();
            FXMLLoader inboxLoader = new FXMLLoader(getClass().getResource("../resources/main/inbox.fxml"));
            mailStage.setTitle("Mail Box");
            mailStage.setScene(new Scene(inboxLoader.load()));
            mailStage.show();

            InboxController inboxController = inboxLoader.getController();
            inboxController.setAccount(accountText.getText());
            inboxController.setSocket(socket);
            inboxController.setOutStream(outStream);
            inboxController.initInbox();

            Stage logInStage = (Stage) logInBtn.getScene().getWindow();
            logInStage.close();
          }else{
            loadAlertStage("Account or password wrong");
            socket.close();
          }

        }catch (IOException e){e.printStackTrace();}
    }
  }

  @FXML
  protected void onRegisterLabelClick() throws IOException {
    if( !accountText.getText().matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") ){
      loadAlertStage("Email syntax wrong!");
    }else {
      //todo check on password;
      if (socket == null || socket.isClosed()) {
        socket = new Socket("127.0.0.1", 8189);
        inStream = new ObjectInputStream(socket.getInputStream());
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.flush();
      }
      outStream.writeObject(new Log(new Date(), accountText.getText(), "Try to registration",
      "regis", null));
      outStream.flush();
      if (inStream.readBoolean())
        loadAlertStage("Registration successful!");
      else {
        loadAlertStage("Account already exists!");
        socket.close();
      }
    }
  }

  @FXML
  protected void onAlertButtonClick() throws IOException {
    Stage alertStage = (Stage) alertBtn.getScene().getWindow();
    alertStage.close();
  }

  public void loadAlertStage(String msg) throws IOException {
    Stage alertStage = new Stage();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/main/alert.fxml"));
    alertStage.setScene(new Scene(fxmlLoader.load()));
    LoginController tempCtrl = fxmlLoader.getController();
    tempCtrl.setAlertLbl(msg);
    alertStage.show();
  }

  public void setAlertLbl(String msg) {
    alertLbl.setText(msg);
  }



}