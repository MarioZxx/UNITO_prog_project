package src.client.main;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import src.model.*;

import javafx.scene.control.Button;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WriteController {
  @FXML
  private TextField sendToTxt;
  @FXML
  private TextField sendSubjectTxt;
  @FXML
  private TextArea sendTextTxt;
  @FXML
  private BorderPane writeBordPane;
  @FXML
  private Button noSendBtn;

  @FXML
  private SplitPane sp;

  private Account account;
  private Socket socket;

  public void setParent(SplitPane sp) {
    this.sp = sp;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public void onSendBtnClick(MouseEvent mouseEvent) throws IOException {
    List<String> receivers = new ArrayList<>(
      Arrays.asList(sendToTxt.getText().replaceAll("[ ]*","").split(";"))
    );
    boolean check = true;
    for(String rec : receivers){
      if( !rec.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") ){
        new LoginController().loadAlertStage("Destination email syntax wrong!");
        check = false;
      }
    }
    if(sendSubjectTxt.getText().equals("") && check) {
      new LoginController().loadAlertStage("Subject cannot be empty!");
      check = false;
    }

    if (!socket.isOutputShutdown() && check) { //shutdown by thread when discon.
      NoWriteObjectOutputStream outStream = new NoWriteObjectOutputStream(socket.getOutputStream());
      outStream.flush();
      Email sendingEmail = new Email(Long.toString(new Date().getTime()),
              new Date(),
              account.getEmailAddress(),
              receivers,
              sendSubjectTxt.getText(),
              sendTextTxt.getText());

      String emailAddr = account.getEmailAddress();
      String addr = "";
      for(int i = 0; i < emailAddr.length(); i++){
        addr += emailAddr.charAt(i);
      }
//      String test = "a@a.a";
//      System.out.println("test : " + test + "\naccount.getEmailAddress() : " + account.getEmailAddress());
//      System.out.println("test.equals(account.getEmailAddress()) : " + test.equals(account.getEmailAddress()));
//      System.out.println("test.getClass().equals(account.getEmailAddress().getClass()) : " + test.getClass().equals(account.getEmailAddress().getClass()));
//      outStream.writeObject(new Log(new Date(), account.getEmailAddress(), "Sending email", "send", sendingEmail));// non si sa perchè non posso usare questa call

      outStream.writeObject(new Log(new Date(), addr, "Sending email", "send", sendingEmail));
      outStream.flush();
    }
  }

  public void onNoSendBtnClick(MouseEvent mouseEvent) {
    sp.getItems().remove(1);
  }

  public void replyEmail(Email email){
    sendToTxt.setText(email.getSender() + "; ");
    sendSubjectTxt.setText("Re: " + email.getSubject());
    sendTextTxt.setText("\n\n\n---------- Replied message ---------\n" + email.getText());
    sendToTxt.setEditable(false);
    sendSubjectTxt.setEditable(false);
  }

  public void replyAllEmail(Email email){
    List<String> repliers = new ArrayList<>();
    for(String replier : email.getReceivers()){
      if(!replier.equals(account.getEmailAddress())){
        repliers.add(replier);
      }
    }

    sendToTxt.setText(email.getSender() + "; " + String.join("; ", repliers));
    sendSubjectTxt.setText("Re: " + email.getSubject());
    sendTextTxt.setText("\n\r\n\r\n\r---------- Replied message ---------\n\r" + email.getText());
    sendToTxt.setEditable(false);
    sendSubjectTxt.setEditable(false);
  }

  public void forwardEmail(Email email){
    sendSubjectTxt.setText("Fo: " + email.getSubject());
    sendTextTxt.setText("\n\r\n\r\n\r---------- Forwarded message ---------\n\r" + email.getText());
    sendSubjectTxt.setEditable(false);
  }


}
