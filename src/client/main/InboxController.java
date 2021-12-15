package src.client.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import src.model.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.lang.Thread;

/**
 * Classe Controller
 */

public class InboxController {
  @FXML
  private ImageView accountImage;
  @FXML
  private Label accountLbl;

  @FXML
  public Button exitBtn;
  @FXML
  public Button writeBtn;

  @FXML
  private ListView<Email> emailListView;
  @FXML
  private SplitPane windowSP;

  private Account account;
  private Socket socket;
  private ObjectInputStream inStream;
  private ObjectOutputStream outStream;
  private List<Email> newEmails;

  public void initInbox(){

    try {
      inStream = new NoReadObjectInputStream(socket.getInputStream());
      outStream = new NoWriteObjectOutputStream(socket.getOutputStream());
    }catch (IOException e){e.printStackTrace();}

      new Thread(()->{
        boolean start = true;
        for(int prova = 0; prova < 10 && !socket.isClosed(); prova++ ){ //test version
//        while (!socket.isClosed()) {  //todo finally version
          try {
            Object inputObject = inStream.readObject(); //it's blocked here

            if (inputObject.getClass().equals(String.class)) {  //if read a String
              switch ((String) inputObject) {
                case "disconnected" -> {
                  javaFXThread("dis");
                  boolean tryReconnect = true;
                  int times = 1;
                  socket.shutdownOutput();
                  while (tryReconnect && !socket.isClosed()) {
                    try {
                      Thread.sleep(5000);
                      socket = new Socket("127.0.0.1", 8189); //try to reconnect every 5secs

                      tryReconnect = false;
                      inStream = new ObjectInputStream(socket.getInputStream());
                      outStream = new ObjectOutputStream(socket.getOutputStream());
                      outStream.flush();
                      javaFXThread("con");
                      System.out.println("reconnected");
                      outStream.writeObject(new Log(new Date(), account.getEmailAddress(), "Reconnected",
                      "reco", null));
                      outStream.flush();

                    } catch (ConnectException | InterruptedException ce) {
                      System.out.println("try " + times++);
                    }
                  }
                }
                case "noSend" -> javaFXThread("noSend");
              }

            } else {  //read a List
                newEmails = (List<Email>) inputObject;
                if(start) {
                  account.saveNewEmails(newEmails); //it's possible because javaFX isn't totally loaded
                  start = false;
                }
                else javaFXThread("new");
            }
          } catch (IOException | ClassNotFoundException e) {  //exit by the exitBtn
            e.printStackTrace();
            System.out.println("Client exit itself.");
          }
        }
      }).start();
  }

  public void setSocket (Socket socket) {
    this.socket = socket;
  }

  public void setOutStream (ObjectOutputStream outStream){
    this.outStream = outStream;
  }

  public void setAccount(String mail) {
    accountLbl.setText(mail);
    account = new Account(mail);
    //binding tra emailList e inboxProperty
    emailListView.itemsProperty().bind(account.inboxProperty());
    emailListView.setOnMouseClicked(this::showSelectedEmail);
  }

  /**
   * chiudi la finestra
   */
  @FXML
  protected void onExitBtnClick(){
    try{
      //so close also the thread
      if (!socket.isOutputShutdown()) { //shutdown by thread
        outStream = new NoWriteObjectOutputStream(socket.getOutputStream());
        outStream.writeObject(new Log(new Date(), account.getEmailAddress(), "exit success", "exit", null));
      }else{
        System.out.println("Client closed without connection");
      }
      socket.close();
    }catch (IOException e){e.printStackTrace();}
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
      writeController.setSocket(socket);
    }catch (IOException e){
      e.printStackTrace();
    }

  }

  /**
   * Mostra la mail selezionata nella vista
   */
  protected void showSelectedEmail(MouseEvent mouseEvent) {
    Email email = emailListView.getSelectionModel().getSelectedItem();
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
        emailController.setSocket(socket);
        emailController.setEmail(email);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void javaFXThread(String msg) throws IOException {
    new Thread(()-> Platform.runLater(() -> {
      try {
        switch (msg) {
          case "dis" -> {
            new LoginController().loadAlertStage("Disconnected from the server.");
            accountImage.setImage(new Image("file:src/client/resources/main/images/loading.gif"));
            writeBtn.setVisible(false);
          }
          case "con" -> {
            new LoginController().loadAlertStage("Reconnection successful.");
            accountImage.setImage(new Image("file:src/client/resources/main/images/logo.png"));
            writeBtn.setVisible(true);
          }
          case "noSend" -> new LoginController().loadAlertStage("Some receivers not exist.");
          case "new" -> {
            new LoginController().loadAlertStage("Received a new email.");
            account.saveNewEmails(newEmails);
          }
        }
      } catch (IOException e) {e.printStackTrace();}
    })).start();
  }


}
