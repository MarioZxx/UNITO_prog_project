package src.server.main;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerController {
  @FXML
  private Button exitBtn;
  @FXML
  private Button exportBtn;
  @FXML
  private TextArea logTxt;

  Date date;

  @FXML
  public void initialize() {
    logTxt.appendText(  new Date() + "Inizializzato il server.");
    try {
      int i = 1;
      ServerSocket s = new ServerSocket(8189);

      while (true) {
        Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
        System.out.println("Spawning " + i);
        Runnable r = new ServerThread(incoming);
        new Thread(r).start();
        i++;
      }
    }
    catch (IOException e) {e.printStackTrace();}
  }

  public void onExitBtnClick() {
    Stage serverStage = (Stage)exitBtn.getScene().getWindow();
    serverStage.close();
  }

  public void onExportBtnClick() {
  }
}

class ServerThread implements Runnable {

  private Socket incoming;

  /**
   Constructs a handler.
   @param in the incoming socket
   */
  public ServerThread(Socket in) {
    incoming = in;
  }

  public void run() {
    try {
      try {
        ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());
        OutputStream outStream = incoming.getOutputStream();

        PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);

        try {
          Date date = ((Date)inStream.readObject());
          System.out.println("Echo: " + date.toString());
          out.println("Echo: " + date.toString());
        } catch (ClassNotFoundException e) {System.out.println(e.getMessage());}

      }
      finally {
        incoming.close();
      }
    }
    catch (IOException e) {e.printStackTrace();}
  }

}
