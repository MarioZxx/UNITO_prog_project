package main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import model.Account;
import model.Email;

import java.util.List;

/**
 * Classe Controller
 */

public class MailController {
  @FXML
  private Label lblFrom;

  @FXML
  private Label lblTo;

  @FXML
  private Label lblSubject;

  @FXML
  private Label lblUsername;

  @FXML
  private TextArea txtEmailContent;

  @FXML
  private ListView<Email> lstEmails;

  private Account model;
  private Email selectedEmail;
  private Email emptyEmail;

  @FXML
  public void initialize(){

  }

  /**
   * Elimina la mail selezionata
   */
  @FXML
  protected void onDeleteButtonClick() {

  }

  /**
   * Mostra la mail selezionata nella vista
   */
  protected void showSelectedEmail(MouseEvent mouseEvent) {

  }

  /**
   * Aggiorna la vista con la mail selezionata
   */
  protected void updateDetailView(Email email) {

  }

}
