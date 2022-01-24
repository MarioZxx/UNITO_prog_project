package src.client.main;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import src.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
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
  private WriteController writeController;

  public void initInbox(){

    try {
      inStream = new NoReadObjectInputStream(socket.getInputStream());
      outStream = new NoWriteObjectOutputStream(socket.getOutputStream());
      outStream.flush();
    }catch (IOException e){e.printStackTrace();}

      new Thread(()->{
        boolean start = true;
//        for(int prova = 0; prova < 10 && !socket.isClosed(); prova++ ){ //test version
        while (!socket.isClosed()) {  //final version
          try {
            try {
              Object inputObject = inStream.readObject(); //it's blocked here
//              System.out.println("inputObject.getClass: " + inputObject.getClass());
              if (inputObject instanceof String) {  //if read a String
                switch ((String) inputObject) {
                  case "disconnected" -> {
                    javaFXThread("dis");
                    boolean tryReconnect = true;
                    int times = 1;
                    socket.shutdownOutput();
                    while (tryReconnect && !socket.isClosed()) {
                      try {
                        Thread.sleep(5000);
                        socket = new Socket("127.0.0.1", 8189); //try to reconnect every 5 secs

                        tryReconnect = false;
                        inStream = new ObjectInputStream(socket.getInputStream());
                        outStream = new ObjectOutputStream(socket.getOutputStream());
                        outStream.flush();
                        javaFXThread("con");
                        System.out.println("reconnected");
                        outStream.writeObject(new Log(new Date(), account.getEmailAddress(), "Reconnected",
                        "reco", null));
                        outStream.flush();

                        if(writeController != null) writeController.setSocket(socket);

                      } catch (ConnectException | InterruptedException ce) {
                        System.out.println("try " + times++);
                      }
                    }
                  }
                  case "noSend" -> javaFXThread("noSend");
                  case "yesSend" -> javaFXThread("yesSend");
                }

              } else {  //read a List
                if (inputObject instanceof List) {
                  newEmails = (List<Email>) inputObject;
                  if (start) {
                    account.saveNewEmails(newEmails); //it's possible because javaFX isn't totally loaded
                    start = false;
                  } else javaFXThread("new");
                }
              }
            } catch (SocketException e) {  //exit by the exitBtn
              System.out.println("Client exit itself.");
            }
          } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
          }
        }
      }).start();
  }

  public void setSocket (Socket socket) {
    this.socket = socket;
  }

  public void setAccount(String mail) {
    accountLbl.setText(mail);
    account = new Account(mail);
    //binding between emailListView and ObservableList<Email> in account
    emailListView.itemsProperty().bind(account.inboxProperty());
    emailListView.setOnMouseClicked(this::showSelectedEmail);
  }

  /**
   * close the window
   */
  @FXML
  protected void onExitBtnClick(){
    try{
      //so close also the thread
      if (!socket.isOutputShutdown()) { //shutdown by thread because server is disconnected
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

      writeController = writeLoader.getController();
      writeController.setParent(windowSP);
      writeController.setAccount(account);
      writeController.setSocket(socket);
    }catch (IOException e){
      e.printStackTrace();
    }

  }

  /**
   * Show the email selected in the ListView
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
        switch (msg) {
          case "dis" -> {
            createPopup("Disconnected from the server.");
            accountImage.setImage(new Image("file:src/client/resources/main/images/loading.gif"));
            writeBtn.setVisible(false);
          }
          case "con" -> {
            createPopup("Reconnection successful.");
            accountImage.setImage(new Image("file:src/client/resources/main/images/logo.png"));
            writeBtn.setVisible(true);
          }
          case "noSend" -> createPopup("Some receivers not exist.");
          case "yesSend" -> createPopup("Email is sent");
          case "new" -> {
            account.saveNewEmails(newEmails);
            createPopup("Received new email/s.");
          }
        }
    })).start();
  }

  private void createPopup(String msg){
    Label label = new Label(msg);
    label.setStyle("-fx-background-color: rgba(140,140,140);" +
                   "-fx-padding: 15;" +
                   "-fx-border-radius: 25;" +
                   "-fx-background-radius: 25;" +
                   "-fx-font-size: 15;");
    label.setTextFill(Color.rgb(255,255,255));

    Stage stage = (Stage) accountImage.getScene().getWindow();
    Popup popup = new Popup();
    popup.setAutoFix(true);
    popup.setAutoHide(true);
    popup.setHideOnEscape(true);
    popup.getContent().add(label);
    popup.setOnShown(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent e) {
        popup.setX(stage.getX() + stage.getWidth()/2 - popup.getWidth()/2);
        popup.setY(stage.getY() + stage.getHeight()/2 - popup.getHeight()/2);
      }
    });
    popup.show(stage);
  }


}
