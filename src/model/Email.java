package src.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Email implements Serializable {

  private String id;
  private Date emailDate;
  private String sender;
  private List<String> receivers;
  private String subject;
  private String text;

  /**
   * Constructor of the class.
   * @param id         id of the mail
   * @param sender     sender's email
   * @param receivers  receivers' email
   * @param subject    mail's subject
   * @param text       mail's text
   * @param emailDate       date the email was sent
   */

  public Email(String id, Date emailDate, String sender, List<String> receivers, String subject, String text) {
    this.id = id;
    this.emailDate = emailDate;
    this.sender = sender;
    this.receivers = new ArrayList<>(receivers);
    this.subject = subject;
    this.text = text;
  }

  public String getId() {
    return id;
  }

  public Date getEmailDate() {
    return emailDate;
  }

  public String getSender() {
    return sender;
  }

  public List<String> getReceivers() {
    return receivers;
  }

  public String getSubject() {
    return subject;
  }

  public String getText() {
    return text;
  }

  /**
   * @return      email information
   */
  @Override
  public String toString() {
    return String.join(" - ", List.of(this.sender, this.subject));
  }
}
