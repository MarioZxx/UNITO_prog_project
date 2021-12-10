package src.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Email {

  private String id;
  private String sender;
  private List<String> receivers;
  private String subject;
  private String text;
  private Date emailDate;

  private Email() {}


  /**
   * Constructor of the class.
   * @param id         id of the mail
   * @param sender     sender's email
   * @param receivers  receivers' email
   * @param subject    mail's subject
   * @param text       mail's text
   * @param emailDate       date the email was sent
   */

  public Email(String id, String sender, List<String> receivers, String subject, String text, Date emailDate) {
    this.id = id;
    this.sender = sender;
    this.receivers = new ArrayList<>(receivers);
    this.subject = subject;
    this.text = text;
    this.emailDate = emailDate;
  }

  public String getId() {
    return id;
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

  public Date getEmailDate() {
    return emailDate;
  }

  /**
   * @return      email information short
   */
  @Override
  public String toString() {
    return String.join(" - ", List.of(this.sender,this.subject));
  }
}