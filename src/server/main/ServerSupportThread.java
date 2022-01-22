package src.server.main;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import src.model.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// a support thread who will call other thread to connect with client
public class ServerSupportThread implements Runnable{
  private ServerSocket serverSocket;
  private TextArea logTxt;


  public ServerSupportThread(ServerSocket serverSocket, TextArea logTxt) {
    this.serverSocket = serverSocket;
    this.logTxt = logTxt;
  }

  public void run() {
    int i = 1;
    List<Socket> socketArr = new ArrayList<>();
    List<Runnable> runnableArr = new ArrayList<>();
    try {
      while (!serverSocket.isClosed()) {
        Socket socket = serverSocket.accept();  //questo thread si blocca qui, fino quando disconnetto
        System.out.println("Client number:  " + i++);
        Runnable r = new ServerThread(socket, logTxt, runnableArr);
        socketArr.add(socket);
        new Thread(r).start();
        runnableArr.add(r);
      }
    }catch (IOException e){ //ServerSocket is closed
      try {
        for(Socket closeSocket : socketArr) {
          if (!closeSocket.isClosed()) {
            NoWriteObjectOutputStream outStream = new NoWriteObjectOutputStream(closeSocket.getOutputStream());
            outStream.writeObject(new String("disconnected"));
            outStream.flush();
            closeSocket.close();
          }
        }
      }catch (IOException err){err.printStackTrace();}
    }
    logTxt.appendText(new Log(new Date(), "SERVER", "disconnected", null, null).toString());
  }

}

class ServerThread implements Runnable {

  private Socket socket;
  private TextArea logTxt;
  private List<Runnable> runnableArr;
  private String account;

  public ServerThread(Socket socket, TextArea logTxt, List<Runnable> runnableArr) {
    this.socket = socket;
    this.logTxt = logTxt;
    this.runnableArr = runnableArr;
  }

  public String getAccount(){
    return account;
  }

  public Socket getSocket(){
    return socket;
  }

  public void run() {
    try {
      ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
      outStream.flush();
      ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

      while (!socket.isClosed()) {
        Log log = (Log) inStream.readObject();
//        logTxt.appendText(log.toString());
        appendToTxtArea(log);
        account = log.getUser().replaceAll("[-+.]","_");

        switch (log.getOperation()) {
          case "login" -> loginHandler(outStream, inStream, log);

          case "regis" -> {
            Path path = Paths.get("src/server/resources/account/" + account);
            try {
              Files.createDirectory(path);
              outStream.writeBoolean(true);
              outStream.flush();
              String passMD5 = (String) inStream.readObject();
              FileWriter fw = new FileWriter( path + "/" + account +  ".pass",false);
              fw.write(passMD5);
              fw.close();
              appendToTxtArea(new Log(new Date(), "SERVER", log.getUser() + " registration success",
              null, null));
            } catch (FileAlreadyExistsException faee) {
              outStream.writeBoolean(false);
              outStream.flush();
              appendToTxtArea(new Log(new Date(), "SERVER", log.getUser() + " registration failed",
              null, null));
            }
          }

          case "send" -> sendHandler(outStream, log);

          case "delete" -> {
            String deleteEmailId = log.getEmail().getId();
            File deleteFile = new File("src/server/resources/account/" + account + "/" +
            deleteEmailId + ".xml");
            deleteFile.delete();
          }

          case "exit" -> socket.close();
        }

      }//end loop on readObject

    } catch (IOException | ClassNotFoundException e) {  //readObject.exp on ServerSocket.close
      try {
        socket.close();
      } catch (IOException err) {err.printStackTrace();}
    }

  }

  private void loginHandler(ObjectOutputStream outStream, ObjectInputStream inStream, Log log) throws IOException, ClassNotFoundException {
    File folder = new File("src/server/resources/account/" + account);
    //check if the account exists
    if (folder.listFiles() == null) {
      outStream.writeBoolean(false);
      outStream.flush();
      appendToTxtArea(new Log(new Date(), "SERVER", log.getUser() + " not exists",
      null, null));
      return;
    }

    //check if the password is correct
    String inputPass = (String) inStream.readObject();
    FileReader passFileReader = new FileReader(new File("src/server/resources/account/" + account
            + "/" + account + ".pass"));
    char[] pass = new char[32];
    passFileReader.read(pass);
    if(!String.valueOf(pass).equals(inputPass)){
      outStream.writeBoolean(false);
      outStream.flush();
      appendToTxtArea(new Log(new Date(), "SERVER", log.getUser() + " password wrong",
      null, null));
      return;
    }

    //account and password are correct, so update the emailsList
    outStream.writeBoolean(true);
    outStream.flush();
    appendToTxtArea(new Log(new Date(), "SERVER", log.getUser() + " login success",
    null, null));
    List<File> emailsListFile = List.of(folder.listFiles());
    outStream.writeObject(new XMLReadWriter().filesToEmails(emailsListFile));
    outStream.flush();
  }

  private void sendHandler(ObjectOutputStream outStream, Log log) throws IOException {
    Email sendingEmail = log.getEmail();
    List<String> receivers = sendingEmail.getReceivers();
    boolean check = true;
    //check if all receivers exist
    for(String rec : receivers){
      File folderCheck = new File("src/server/resources/account/" + rec.replaceAll("[-+.]","_"));
      if(!folderCheck.exists() && !folderCheck.isDirectory()){
        outStream.writeObject(new String("noSend"));
        outStream.flush();
        check = false;
        appendToTxtArea(new Log(new Date(), "SERVER", log.getUser() + " sending failed because some receiver not exist.",
                null, null));
        break;
      }
    }

    if(sendingEmail != null && check){
      //save the msg in their directory
      new XMLReadWriter().emailToFile(sendingEmail);
      //get all opened client sockets
      HashMap<String, Socket> currClients = getCurrClients(runnableArr, receivers);
      //send the email to clients
      for(Socket s : currClients.values()){
        NoWriteObjectOutputStream tempOutStream = new NoWriteObjectOutputStream(s.getOutputStream());
        tempOutStream.writeObject(List.of(sendingEmail));
        tempOutStream.flush();
      }
      for(String rec : currClients.keySet()){
        appendToTxtArea(new Log(new Date(), "SERVER", rec + " received an email",
        null, null));
      }
      outStream.writeObject(new String("yesSend"));
      outStream.flush();
    }

  }

  private HashMap<String, Socket> getCurrClients(List<Runnable> runnableArr, List<String> receivers) {
    HashMap<String, Socket> res = new HashMap<>();
    Set<String> recSet = new HashSet<>();
    for (String rec : receivers){
      rec = rec.replaceAll("[-+.]","_");
      recSet.add(rec);
    }
    for (Runnable r : runnableArr){
      String tempAcc = ((ServerThread)r).getAccount();
      if(recSet.contains(tempAcc)){
        res.put(tempAcc, ((ServerThread)r).getSocket());
      }
    }
    return res;
  }

  private void appendToTxtArea(Log log) {
    new Thread(()-> Platform.runLater(() -> {
      logTxt.appendText(log.toString());
    })).start();
  }

}
