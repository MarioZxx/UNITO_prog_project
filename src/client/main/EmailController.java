package src.client.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import src.model.Email;
import src.model.Account;
import src.model.Log;
import src.model.NoWriteObjectOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class EmailController {
  @FXML
  private SplitPane windowSP;
  @FXML
  private Label fromLbl;
  @FXML
  private Label toLbl;
  @FXML
  private Label subjectLbl;
  @FXML
  private TextArea emailContentTxt;

  private Account account;
  private Email email;
  private Socket socket;

  public void setParent(SplitPane sp) {
    this.windowSP = sp;
  }

  public void setAccount(Account account){
    this.account = account;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public void setEmail(Email email) {
    this.email = email;
    if(email != null) {
      fromLbl.setText(email.getSender());
      toLbl.setText(String.join("; ", email.getReceivers()));
      subjectLbl.setText(email.getSubject());
      emailContentTxt.setText(email.getText());
    }
  }

  @FXML
  protected void onReplyBtnClick() {
    try {
      WriteController writeController = writeSupport();
      writeController.replyEmail(email);
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  @FXML
  protected void onReplyAllBtnClick() {
    try {
      WriteController writeController = writeSupport();
      writeController.replyAllEmail(email);
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  @FXML
  protected void onForwardBtnClick() {
    try {
      WriteController writeController = writeSupport();
      writeController.forwardEmail(email);
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  private WriteController writeSupport() throws IOException{

    FXMLLoader replyLoader = new FXMLLoader(getClass().getResource("../resources/main/write.fxml"));
    windowSP.getItems().set(1, replyLoader.load());

    WriteController writeController = replyLoader.getController();
    writeController.setParent(windowSP);
    writeController.setAccount(account);
    writeController.setSocket(socket);
    return writeController;
  }

  public void onDeleteBtnClick(MouseEvent mouseEvent) throws IOException { //socket
    if (!socket.isOutputShutdown()) { //shutdown by thread
      NoWriteObjectOutputStream outStream = new NoWriteObjectOutputStream(socket.getOutputStream());
      outStream.writeObject(new Log(new Date(), account.getEmailAddress(), "Delete email id: " + email.getId(),
      "delete", email));
      account.deleteEmail(email);
      windowSP.getItems().remove(1);
    }
  }
}
