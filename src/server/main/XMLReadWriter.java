package src.server.main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import src.model.Email;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XMLReadWriter {

  public List<Email> filesToEmails(List<File> emailsFiles) {
    List<Email> res = new ArrayList<>();
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      for (File file : emailsFiles) {
        if(!file.getPath().endsWith(".xml")) continue;
        Document doc = db.parse(file);

        // get text
        String id = doc.getElementsByTagName("id").item(0).getTextContent();

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String emailDateStr = doc.getElementsByTagName("emailDate").item(0).getTextContent();
        Date emailDate = df.parse(emailDateStr);

        String sender = doc.getElementsByTagName("sender").item(0).getTextContent();
        List<String> receivers = new ArrayList<>();
        for (int j = 0; j < doc.getElementsByTagName("receiver").getLength(); j++) {
          receivers.add(doc.getElementsByTagName("receiver").item(j).getTextContent());
        }

        String subject = doc.getElementsByTagName("subject").item(0).getTextContent();
        String text = doc.getElementsByTagName("text").item(0).getTextContent();

        Email email = new Email(id, emailDate, sender, receivers, subject, text);
        res.add(email);
      }

    } catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
      e.printStackTrace();}
    return res;
  }

  public void emailToFile(Email email){

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
