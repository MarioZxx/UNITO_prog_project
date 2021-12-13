package src.server.main;

import javafx.scene.control.TextArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import src.model.Email;
import src.model.NoWriteObjectOutputStream;
import src.model.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
        System.out.println("Spawning " + i++);
        Runnable r = new ServerThread(socket, logTxt, runnableArr);
        socketArr.add(socket);
        new Thread(r).start();
        runnableArr.add(r);
      }
    }catch (IOException e){
      try {
        for(Socket closeSocket : socketArr) {
          if (!closeSocket.isClosed()) {
            NoWriteObjectOutputStream outStream = new NoWriteObjectOutputStream(closeSocket.getOutputStream());
            outStream.writeObject("Connesione server caduta.");
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
        System.out.println(runnableArr.size() + " prova " + runnableArr.toString());
        Log log = ((Log) inStream.readObject());
        logTxt.appendText(log.toString());
        account = log.getAccount().replaceAll("[-+.]","_");

        switch (log.getOperation()){  //TODO implement password case
          case "login":
            File folder = new File("src/server/resources/account/" + account);
            if(folder.listFiles() == null){
              outStream.writeBoolean(false);
              outStream.flush();
              break;
            }
            outStream.writeBoolean(true);
            outStream.flush();
            logTxt.appendText(new Log(new Date(), "SERVER", log.getAccount() + " login success",
                    null, null).toString());
            List<File> emailsListFile = List.of(folder.listFiles());
            outStream.writeObject(emailsListFile);
            outStream.flush();
            break;
          case "regis":
            Path path = Paths.get("src/server/resources/account/" + account);
            try {
              Files.createDirectory(path);
              outStream.writeBoolean(true);
            }catch (FileAlreadyExistsException faee){
              outStream.writeBoolean(false);
            }finally {
              outStream.flush();
            }
            break;
          case "send":
            Email sendingEmail = log.getEmail();
            List<String> receivers = sendingEmail.getReceivers();
            boolean check = true;
            for(String rec : receivers){
              File folderCheck = new File("src/server/resources/account/" + rec.replaceAll("[-+.]","_"));
              if(!folderCheck.exists() && !folderCheck.isDirectory()){
                outStream.writeObject(false);
                outStream.flush();
                check = false;
                break;
              }
            }
            List<Socket> currClients = getCurrClients(runnableArr, receivers);
            System.out.println(currClients);
            if(sendingEmail != null && check){
              outStream.writeObject(true);
              outStream.flush();
              emailToDOM(sendingEmail);
            }
            break;
          case "delete":
            String deleteEmailId = log.getEmail().getId();
            File deleteFile = new File("src/server/resources/account/" + account + "/" +
                    deleteEmailId + ".xml");
            deleteFile.delete();
            break;
          case "exit":
            socket.close();
            break;
        }

      }

    } catch (IOException | ClassNotFoundException e) {  //readObject.exp on ServerSocket.close
      try {
        socket.close();
      } catch (IOException err) {err.printStackTrace();}
    }

  }

  private List<Socket> getCurrClients(List<Runnable> runnableArr, List<String> receivers) {
    List<Socket> res = new ArrayList<>();
    Set<String> recSet = new HashSet<>();
    for (String rec : receivers){
      recSet.add(rec);
    }
    for (Runnable r : runnableArr){
      String tempAcc = ((ServerThread)r).getAccount();
      if(recSet.contains(tempAcc)){
        res.add(((ServerThread)r).getSocket());
      }
    }
    return res;
  }

  private void emailToDOM(Email email){

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document dom = db.newDocument();

      // root node
      Element rootEle = dom.createElement("email");
      Element nodeEle;

      // create nodes and append them to the root
      nodeEle = dom.createElement("id");
      nodeEle.appendChild(dom.createTextNode(email.getId()));
      rootEle.appendChild(nodeEle);

      nodeEle = dom.createElement("sender");
      nodeEle.appendChild(dom.createTextNode(email.getSender()));
      rootEle.appendChild(nodeEle);

      nodeEle = dom.createElement("receivers");
      Element receiver;
      for(String rec : email.getReceivers()){
        receiver = dom.createElement("receiver");
        receiver.appendChild(dom.createTextNode(rec));
        nodeEle.appendChild(receiver);
      }
      rootEle.appendChild(nodeEle);

      nodeEle = dom.createElement("subject");
      nodeEle.appendChild(dom.createTextNode(email.getSubject()));
      rootEle.appendChild(nodeEle);

      nodeEle = dom.createElement("emailDate");
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      nodeEle.appendChild(dom.createTextNode(df.format(email.getEmailDate())));
      rootEle.appendChild(nodeEle);

      nodeEle = dom.createElement("text");
      nodeEle.appendChild(dom.createTextNode(email.getText()));
      rootEle.appendChild(nodeEle);

      rootEle.normalize();
      dom.appendChild(rootEle);

      Transformer tr = TransformerFactory.newInstance().newTransformer();
      tr.setOutputProperty(OutputKeys.INDENT, "yes");
      tr.setOutputProperty(OutputKeys.METHOD, "xml");
      tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      // send DOM to file
      for(String rec : email.getReceivers()){
        rec = rec.replaceAll("[-+.]","_");
        tr.transform(new DOMSource(dom),
                new StreamResult(new FileOutputStream("src/server/resources/account/" + rec + "/" +
                email.getId() + ".xml")));
      }

    } catch (ParserConfigurationException | IOException | TransformerException e) {
      e.printStackTrace();
    }
  }


}
