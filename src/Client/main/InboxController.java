package src.Client.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import src.model.Email;
import src.model.Account;

import java.io.IOException;

/**
 * Classe Controller
 */

public class InboxController {

  @FXML
  private Label accountLbl;

  @FXML
  public Button exitBtn;
  @FXML
  public Button writeBtn;
  @FXML
  public Button replyBtn;
  @FXML
  public Button replyAllBtn;
  @FXML
  public Button forwardBtn;
  @FXML
  public Button deleteBtn;
  @FXML
  private BorderPane messageBordPane;


  @FXML
  private ListView<Email> emailsList;
  @FXML
  private SplitPane windowSP;

  private Account account;

  public void setUserMail (String mail) {
    accountLbl.setText(mail);
    account = new Account(accountLbl.getText());
    account.getEmails();

    //binding tra emailList e inboxProperty
    emailsList.itemsProperty().bind(account.inboxProperty());
    emailsList.setOnMouseClicked(this::showSelectedEmail);
  }


  /**
   * chiudi la finestra
   */
  @FXML
  protected void onExitBtnClick() {
    Stage exitStage = (Stage) exitBtn.getScene().getWindow();
    exitStage.close();
  }

  @FXML
  protected void onWriteBtnClick() {
    FXMLLoader writeLoader = new FXMLLoader(getClass().getResource("../resources/main/write.fxml"));

    try {

      if(windowSP.getItems().size() > 1) {
        windowSP.getItems().set(1, writeLoader.load());
      }
      else {
        windowSP.getItems().add(writeLoader.load());
        windowSP.setDividerPositions(0.38);
      }

      WriteController writeController = writeLoader.getController();
      writeController.setParent(windowSP);
      writeController.setAccount(account);
    }catch (IOException e){
      e.printStackTrace();
    }

  }



  /**
   * Mostra la mail selezionata nella vista
   */
  protected void showSelectedEmail(MouseEvent mouseEvent) {
    Email email = emailsList.getSelectionModel().getSelectedItem();
    if(email != null) {
      FXMLLoader emailLoader = new FXMLLoader(getClass().getResource("../resources/main/email.fxml"));
      try {

        if (windowSP.getItems().size() > 1) {
          windowSP.getItems().set(1, emailLoader.load());
        } else {
          windowSP.getItems().add(emailLoader.load());
          windowSP.setDividerPositions(0.38);
        }

        EmailController emailController = emailLoader.getController();
        emailController.setParent(windowSP);
        emailController.setAccount(account);
        emailController.setEmail(email);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
