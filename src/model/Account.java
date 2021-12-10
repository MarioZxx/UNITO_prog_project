package src.model;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;

public class Account{

  private final ListProperty<Email> inbox;
  private final ObservableList<Email> inboxContent;
  private final String emailAddress;

  /**
   * Costruttore della classe.
   *
   * @param emailAddress   indirizzo email
   *
   */

  public Account(String emailAddress) {
    this.inboxContent = FXCollections.observableList(new LinkedList<>());
    this.inbox = new SimpleListProperty<>();
    this.inbox.set(inboxContent);
    this.emailAddress = emailAddress;
  }

  /**
   * @return      lista di email
   *
   */
  public ListProperty<Email> inboxProperty() {
    return inbox;
  }

  /**
   *
   * @return   indirizzo email della casella postale
   *
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   *
   * @return   elimina l'email specificata
   *
   */
  public void deleteEmail(Email email) {
    inboxContent.remove(email);
  }

  /**
   *genera email random da aggiungere alla lista di email, ese verranno mostrate nella ui
   */
  public void getEmails() {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File("src/client/resources/mails/abcdef.xml"));

      for (int i = 0; i < 1; i++) { //da cambiare in while, finchè nel socket c'è ancora file, itera

        // get text
        String id = doc.getElementsByTagName("id").item(0).getTextContent();
        String sender = doc.getElementsByTagName("sender").item(0).getTextContent();
        List<String> receivers = new ArrayList<>();
        for(int j = 0; j < doc.getElementsByTagName("receiver").getLength(); j++){
          receivers.add(doc.getElementsByTagName("receiver").item(j).getTextContent());
        }
        String subject = doc.getElementsByTagName("subject").item(0).getTextContent();
        String text = doc.getElementsByTagName("text").item(0).getTextContent();

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String emailDateStr = doc.getElementsByTagName("emailDate").item(0).getTextContent();
        Date emailDate = df.parse(emailDateStr);

//        System.out.println("Current Element :" + doc.getNodeName());
//        System.out.println("Id : " + id);
//        System.out.println("Sender : " + sender);
//        System.out.println("Receivers : " + receivers);
//        System.out.println("Subject : " + subject);
//        System.out.println("EmailDate : " + emailDate);
//        System.out.println("Text : " + text);

        Email email = new Email(id, sender, receivers, subject, text, emailDate);
        inboxContent.add(email);
      }


    }catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
      e.printStackTrace();
    }
  }
}
