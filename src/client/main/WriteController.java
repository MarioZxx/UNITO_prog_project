package src.client.main;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import src.model.*;

import javafx.scene.control.Button;

import javax.xml.parsers.*;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    NoReadObjectInputStream inStream = new NoReadObjectInputStream(socket.getInputStream());
    NoWriteObjectOutputStream outStream = new NoWriteObjectOutputStream(socket.getOutputStream());
    Email sendingEmail = new Email(Long.toString(new Date().getTime()),
            account.getEmailAddress(),
            receivers,
            sendSubjectTxt.getText(),
            sendTextTxt.getText(),
            new Date());
      outStream.writeObject(new Log(new Date(), account.getEmailAddress(), "Sending email",
              "send", sendingEmail));
      outStream.flush();
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
