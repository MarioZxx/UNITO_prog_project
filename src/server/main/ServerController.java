package src.server.main;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import src.model.*;

public class ServerController {
  @FXML
  private Button exitBtn;
  @FXML
  private Button exportBtn;
  @FXML
  private Button connectBtn;
  @FXML
  private Button disconnectBtn;
  @FXML
  private TextArea logTxt;

  private ServerSocket serverSocket;

  @FXML
  public void initialize(){
    connectBtn.setVisible(true);
    disconnectBtn.setVisible(false);
  }

  @FXML
  protected void onConnectBtnClick() {
    System.out.println("Connecting...");
    logTxt.appendText(new Log(new Date(), "SERVER", "connection started", null, null).toString());
    try {
      serverSocket = new ServerSocket(8189);
      Runnable r = new ServerSupportThread(serverSocket, logTxt);
      new Thread(r).start();
    }
    catch (IOException e) {e.printStackTrace();}
    disconnectBtn.setVisible(true);
    connectBtn.setVisible(false);
  }

  @FXML
  protected void onDisconnectBtnClick() {
    try{
      serverSocket.close();
    }catch (IOException e){
      e.printStackTrace();
    }
    connectBtn.setVisible(true);
    disconnectBtn.setVisible(false);
  }

  public void onExitBtnClick() {
    if(serverSocket != null && !serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Stage serverStage = (Stage)exitBtn.getScene().getWindow();
    serverStage.close();
  }

  public void onExportBtnClick() throws IOException {
    DateFormat df = new SimpleDateFormat("dd_MM_yyyy");
    FileWriter fw = new FileWriter(df.format(new Date()) + ".log",true);
    fw.write(logTxt.getText());
    if (fw!=null) fw.close();
  }


}


